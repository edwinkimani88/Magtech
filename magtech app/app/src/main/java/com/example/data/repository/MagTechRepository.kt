package com.example.data.repository

import com.example.data.db.daos.*
import com.example.data.db.entities.*
import kotlinx.coroutines.flow.Flow
import java.util.Calendar

class MagTechRepository(
    private val itemDao: ItemDao,
    private val customerDao: CustomerDao,
    private val loanDao: LoanDao,
    private val smsLogDao: SmsLogDao,
    private val transactionDao: TransactionDao
) {
    val allItems: Flow<List<ItemEntity>> = itemDao.getAllItems()
    val marketplaceItems: Flow<List<ItemEntity>> = itemDao.getMarketplaceItems()
    val allCustomers: Flow<List<CustomerEntity>> = customerDao.getAllCustomers()
    val allLoans: Flow<List<LoanEntity>> = loanDao.getAllLoans()
    val allSmsLogs: Flow<List<SmsLogEntity>> = smsLogDao.getAllSmsLogs()
    val allTransactions: Flow<List<TransactionEntity>> = transactionDao.getAllTransactions()

    val totalItemCount: Flow<Int> = itemDao.getTotalItemCount()
    val marketplaceCount: Flow<Int> = itemDao.getMarketplaceCount()
    val activeLoanCount: Flow<Int> = loanDao.getActiveLoanCount()

    val totalRevenue: Flow<Double?> = transactionDao.getTotalRevenue()
    val totalLoansDisbursed: Flow<Double?> = transactionDao.getTotalLoansDisbursed()
    val totalDirectPurchases: Flow<Double?> = transactionDao.getTotalDirectPurchases()

    fun getLoansDueToday(): Flow<List<LoanEntity>> {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        val todayStart = calendar.timeInMillis

        calendar.set(Calendar.HOUR_OF_DAY, 23)
        calendar.set(Calendar.MINUTE, 59)
        calendar.set(Calendar.SECOND, 59)
        val todayEnd = calendar.timeInMillis

        return loanDao.getLoansDueToday(todayStart, todayEnd)
    }

    fun getOverdueLoans(): Flow<List<LoanEntity>> {
        return loanDao.getOverdueLoans(System.currentTimeMillis())
    }

    suspend fun getCustomerById(id: Long): CustomerEntity? = customerDao.getCustomerById(id)
    suspend fun getCustomerByNationalId(nationalId: String): CustomerEntity? = customerDao.getCustomerByNationalId(nationalId)
    suspend fun getItemById(id: Long): ItemEntity? = itemDao.getItemById(id)
    suspend fun getLoanById(id: Long): LoanEntity? = loanDao.getLoanById(id)

    fun getItemsForCustomer(customerId: Long): Flow<List<ItemEntity>> = itemDao.getItemsForCustomer(customerId)
    fun getLoansForCustomer(customerId: Long): Flow<List<LoanEntity>> = loanDao.getLoansForCustomer(customerId)
    fun getSmsLogsForCustomer(customerId: Long): Flow<List<SmsLogEntity>> = smsLogDao.getSmsLogsForCustomer(customerId)

    suspend fun registerNewLoan(
        customerName: String,
        nationalId: String,
        phoneNumber: String,
        itemName: String,
        category: String,
        brand: String,
        condition: String,
        estimatedMarketValue: Double,
        forcedSaleValue: Double,
        loanAmountGiven: Double,
        totalAmountPayable: Double,
        dueDateMs: Long,
        notes: String,
        photoUrls: List<String>,
        shopLocation: String = "Shop 1"
    ): Long {
        // Find existing customer or create new
        var customer = customerDao.getCustomerByNationalId(nationalId)
        val customerId = if (customer != null) {
            customer.id
        } else {
            customerDao.insertCustomer(
                CustomerEntity(
                    fullName = customerName,
                    nationalId = nationalId,
                    phoneNumber = phoneNumber,
                    notes = notes
                )
            )
        }

        val photoJson = photoUrls.joinToString(",")

        // Create Item
        val itemId = itemDao.insertItem(
            ItemEntity(
                itemName = itemName,
                category = category,
                brand = brand,
                condition = condition,
                estimatedMarketValue = estimatedMarketValue,
                forcedSaleValue = forcedSaleValue,
                notes = notes,
                photoUrlsJson = photoJson,
                status = "Active Loan",
                entryType = "LOAN",
                isPublishedToMarketplace = true,
                marketplacePrice = estimatedMarketValue,
                customerId = customerId,
                shopLocation = shopLocation
            )
        )

        // Create Loan
        val loanId = loanDao.insertLoan(
            LoanEntity(
                itemId = itemId,
                customerId = customerId,
                amountGiven = loanAmountGiven,
                totalPayable = totalAmountPayable,
                paidAmount = 0.0,
                dateIssued = System.currentTimeMillis(),
                dueDate = dueDateMs,
                status = "ACTIVE",
                shopLocation = shopLocation
            )
        )

        // Record Transaction
        transactionDao.insertTransaction(
            TransactionEntity(
                type = "LOAN_DISBURSED",
                amount = loanAmountGiven,
                itemId = itemId,
                customerId = customerId,
                description = "Loan disbursed for $itemName to $customerName",
                shopLocation = shopLocation
            )
        )

        return loanId
    }

    suspend fun registerDirectPurchase(
        itemName: String,
        category: String,
        brand: String,
        condition: String,
        estimatedMarketValue: Double,
        purchasePrice: Double,
        notes: String,
        photoUrls: List<String>,
        sellerName: String,
        sellerPhone: String,
        shopLocation: String = "Shop 1"
    ): Long {
        var customerId: Long? = null
        if (sellerName.isNotBlank() && sellerPhone.isNotBlank()) {
            val existing = customerDao.getCustomerByPhone(sellerPhone)
            customerId = existing?.id ?: customerDao.insertCustomer(
                CustomerEntity(
                    fullName = sellerName,
                    nationalId = "N/A",
                    phoneNumber = sellerPhone,
                    notes = "Direct Item Seller"
                )
            )
        }

        val photoJson = photoUrls.joinToString(",")

        val itemId = itemDao.insertItem(
            ItemEntity(
                itemName = itemName,
                category = category,
                brand = brand,
                condition = condition,
                estimatedMarketValue = estimatedMarketValue,
                forcedSaleValue = purchasePrice,
                notes = notes,
                photoUrlsJson = photoJson,
                status = "Purchased",
                entryType = "DIRECT_PURCHASE",
                isPublishedToMarketplace = true,
                marketplacePrice = estimatedMarketValue,
                customerId = customerId,
                shopLocation = shopLocation
            )
        )

        transactionDao.insertTransaction(
            TransactionEntity(
                type = "DIRECT_PURCHASE",
                amount = purchasePrice,
                itemId = itemId,
                customerId = customerId,
                description = "Direct purchase: $itemName",
                shopLocation = shopLocation
            )
        )

        return itemId
    }

    suspend fun recordLoanPayment(loanId: Long, amountPaid: Double) {
        val loan = loanDao.getLoanById(loanId) ?: return
        val newPaid = loan.paidAmount + amountPaid
        val isFullyRedeemed = newPaid >= loan.totalPayable
        val newStatus = if (isFullyRedeemed) "REDEEMED" else loan.status

        val updatedLoan = loan.copy(
            paidAmount = newPaid,
            status = newStatus
        )
        loanDao.updateLoan(updatedLoan)

        // Update item status if redeemed
        if (isFullyRedeemed) {
            val item = itemDao.getItemById(loan.itemId)
            if (item != null) {
                itemDao.updateItem(item.copy(status = "Redeemed", isPublishedToMarketplace = false))
            }
        }

        transactionDao.insertTransaction(
            TransactionEntity(
                type = "LOAN_REPAYMENT",
                amount = amountPaid,
                itemId = loan.itemId,
                customerId = loan.customerId,
                description = "Repayment of KSh ${amountPaid.toInt()} on Loan #$loanId",
                shopLocation = loan.shopLocation
            )
        )
    }

    suspend fun toggleMarketplacePublish(itemId: Long, isPublished: Boolean, price: Double? = null) {
        val item = itemDao.getItemById(itemId) ?: return
        val updated = item.copy(
            isPublishedToMarketplace = isPublished,
            marketplacePrice = price ?: item.marketplacePrice
        )
        itemDao.updateItem(updated)
    }

    suspend fun logSms(customerId: Long, phone: String, message: String, status: String = "SENT") {
        smsLogDao.insertSmsLog(
            SmsLogEntity(
                customerId = customerId,
                phoneNumber = phone,
                messageText = message,
                status = status
            )
        )
    }
}
