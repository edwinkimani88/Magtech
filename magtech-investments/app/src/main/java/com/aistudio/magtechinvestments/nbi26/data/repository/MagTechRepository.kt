package com.aistudio.magtechinvestments.nbi26.data.repository

import com.aistudio.magtechinvestments.nbi26.data.db.daos.*
import com.aistudio.magtechinvestments.nbi26.data.db.entities.*
import com.aistudio.magtechinvestments.nbi26.data.supabase.SupabaseService
import kotlinx.coroutines.flow.Flow
import java.util.Calendar

class MagTechRepository(
    private val itemDao: ItemDao,
    private val customerDao: CustomerDao,
    private val loanDao: LoanDao,
    private val smsLogDao: SmsLogDao,
    private val transactionDao: TransactionDao,
    private val supabaseService: SupabaseService? = null
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
        // Sync customer to Supabase
        val supabaseCustId = supabaseService?.syncCustomerToSupabase(
            fullName = customerName,
            nationalId = nationalId,
            phoneNumber = phoneNumber,
            notes = notes,
            shopLocation = shopLocation
        )

        // Find existing customer or create new in Room
        var customer = customerDao.getCustomerByNationalId(nationalId)
        val customerId = if (customer != null) {
            customer.id
        } else {
            customerDao.insertCustomer(
                CustomerEntity(
                    id = supabaseCustId ?: 0,
                    fullName = customerName,
                    nationalId = nationalId,
                    phoneNumber = phoneNumber,
                    notes = notes
                )
            )
        }

        val photoJson = photoUrls.joinToString(",")

        // Sync Item to Supabase (Uploads photos to Supabase Storage!)
        val supabaseItemId = supabaseService?.syncItemToSupabase(
            itemName = itemName,
            category = category,
            brand = brand,
            condition = condition,
            estimatedMarketValue = estimatedMarketValue,
            forcedSaleValue = forcedSaleValue,
            notes = notes,
            photoUrls = photoUrls,
            status = "Active Loan",
            entryType = "LOAN",
            isPublished = true,
            marketplacePrice = estimatedMarketValue,
            customerId = customerId,
            shopLocation = shopLocation
        )

        // Create Item in Room
        val itemId = itemDao.insertItem(
            ItemEntity(
                id = supabaseItemId ?: 0,
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

        // Sync Loan to Supabase
        val supabaseLoanId = supabaseService?.syncLoanToSupabase(
            itemId = itemId,
            customerId = customerId,
            amountGiven = loanAmountGiven,
            totalPayable = totalAmountPayable,
            paidAmount = 0.0,
            dueDateMs = dueDateMs,
            status = "ACTIVE",
            shopLocation = shopLocation
        )

        // Create Loan in Room
        val loanId = loanDao.insertLoan(
            LoanEntity(
                id = supabaseLoanId ?: 0,
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

        // Record Transaction in Supabase & Room
        val txDesc = "Loan disbursed for $itemName to $customerName"
        supabaseService?.syncTransactionToSupabase(
            type = "LOAN_DISBURSED",
            amount = loanAmountGiven,
            itemId = itemId,
            customerId = customerId,
            description = txDesc,
            shopLocation = shopLocation
        )

        transactionDao.insertTransaction(
            TransactionEntity(
                type = "LOAN_DISBURSED",
                amount = loanAmountGiven,
                itemId = itemId,
                customerId = customerId,
                description = txDesc,
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
            val supabaseCustId = supabaseService?.syncCustomerToSupabase(
                fullName = sellerName,
                nationalId = "N/A",
                phoneNumber = sellerPhone,
                notes = "Direct Item Seller",
                shopLocation = shopLocation
            )

            val existing = customerDao.getCustomerByPhone(sellerPhone)
            customerId = existing?.id ?: customerDao.insertCustomer(
                CustomerEntity(
                    id = supabaseCustId ?: 0,
                    fullName = sellerName,
                    nationalId = "N/A",
                    phoneNumber = sellerPhone,
                    notes = "Direct Item Seller"
                )
            )
        }

        val photoJson = photoUrls.joinToString(",")

        val supabaseItemId = supabaseService?.syncItemToSupabase(
            itemName = itemName,
            category = category,
            brand = brand,
            condition = condition,
            estimatedMarketValue = estimatedMarketValue,
            forcedSaleValue = purchasePrice,
            notes = notes,
            photoUrls = photoUrls,
            status = "Purchased",
            entryType = "DIRECT_PURCHASE",
            isPublished = true,
            marketplacePrice = estimatedMarketValue,
            customerId = customerId,
            shopLocation = shopLocation
        )

        val itemId = itemDao.insertItem(
            ItemEntity(
                id = supabaseItemId ?: 0,
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

        val txDesc = "Direct purchase: $itemName"
        supabaseService?.syncTransactionToSupabase(
            type = "DIRECT_PURCHASE",
            amount = purchasePrice,
            itemId = itemId,
            customerId = customerId,
            description = txDesc,
            shopLocation = shopLocation
        )

        transactionDao.insertTransaction(
            TransactionEntity(
                type = "DIRECT_PURCHASE",
                amount = purchasePrice,
                itemId = itemId,
                customerId = customerId,
                description = txDesc,
                shopLocation = shopLocation
            )
        )

        return itemId
    }

    suspend fun recordLoanPayment(loanId: Long, amountPaid: Double, adminUser: String = "Admin") {
        val loan = loanDao.getLoanById(loanId) ?: return
        val previousBalance = loan.totalPayable - loan.paidAmount
        val newPaid = loan.paidAmount + amountPaid
        val newBalance = (loan.totalPayable - newPaid).coerceAtLeast(0.0)
        val isFullyRedeemed = newBalance <= 0.0
        val newStatus = if (isFullyRedeemed) "PAID" else "PARTIALLY_PAID"

        val updatedLoan = loan.copy(
            paidAmount = newPaid,
            status = newStatus
        )
        loanDao.updateLoan(updatedLoan)

        // Sync payment & updated loan to Supabase
        supabaseService?.syncLoanPaymentToSupabase(
            loanId = loanId,
            paymentAmount = amountPaid,
            previousBalance = previousBalance,
            newBalance = newBalance,
            adminUser = adminUser,
            shopLocation = loan.shopLocation
        )

        // Update item status if redeemed
        if (isFullyRedeemed) {
            val item = itemDao.getItemById(loan.itemId)
            if (item != null) {
                itemDao.updateItem(item.copy(status = "Redeemed", isPublishedToMarketplace = false))
            }
        }

        val txDesc = "Repayment of KSh ${amountPaid.toInt()} on Loan #$loanId"
        supabaseService?.syncTransactionToSupabase(
            type = "LOAN_REPAYMENT",
            amount = amountPaid,
            itemId = loan.itemId,
            customerId = loan.customerId,
            description = txDesc,
            shopLocation = loan.shopLocation,
            adminUser = adminUser
        )

        transactionDao.insertTransaction(
            TransactionEntity(
                type = "LOAN_REPAYMENT",
                amount = amountPaid,
                itemId = loan.itemId,
                customerId = loan.customerId,
                description = txDesc,
                shopLocation = loan.shopLocation
            )
        )
    }

    suspend fun extendLoan(loanId: Long, renewalFee: Double, extensionDays: Int = 14, adminUser: String = "Admin") {
        val loan = loanDao.getLoanById(loanId) ?: return
        val previousDueDate = loan.dueDate
        val newDueDate = previousDueDate + (extensionDays.toLong() * 24 * 60 * 60 * 1000)

        val updatedLoan = loan.copy(
            dueDate = newDueDate,
            status = "EXTENDED"
        )
        loanDao.updateLoan(updatedLoan)

        // Sync renewal to Supabase
        supabaseService?.syncLoanRenewalToSupabase(
            loanId = loanId,
            renewalFee = renewalFee,
            previousDueDateMs = previousDueDate,
            newDueDateMs = newDueDate,
            renewalNumber = 1,
            adminUser = adminUser,
            shopLocation = loan.shopLocation
        )

        val txDesc = "Loan #$loanId Extended for $extensionDays days (Fee: KSh ${renewalFee.toInt()})"
        supabaseService?.syncTransactionToSupabase(
            type = "LOAN_EXTENSION",
            amount = renewalFee,
            itemId = loan.itemId,
            customerId = loan.customerId,
            description = txDesc,
            shopLocation = loan.shopLocation,
            adminUser = adminUser
        )

        transactionDao.insertTransaction(
            TransactionEntity(
                type = "LOAN_EXTENSION",
                amount = renewalFee,
                itemId = loan.itemId,
                customerId = loan.customerId,
                description = txDesc,
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

    suspend fun syncAllDataFromCloud(): Boolean {
        if (supabaseService == null) return false
        return try {
            val customers = supabaseService.fetchCustomersFromSupabase()
            customers.forEach { customerDao.insertCustomer(it) }

            val items = supabaseService.fetchItemsFromSupabase()
            items.forEach { itemDao.insertItem(it) }

            val loans = supabaseService.fetchLoansFromSupabase()
            loans.forEach { loanDao.insertLoan(it) }

            val txs = supabaseService.fetchTransactionsFromSupabase()
            txs.forEach { transactionDao.insertTransaction(it) }

            true
        } catch (e: Exception) {
            false
        }
    }
}

