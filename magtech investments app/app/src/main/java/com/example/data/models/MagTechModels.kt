package com.example.data.models

import java.util.UUID

data class Shop(
    val id: String,
    val name: String,
    val location: String,
    val phone: String
) {
    companion object {
        val SHOP_1 = Shop("shop_1", "MagTech Shop 1 (Chairman Rd)", "Chairman Road, Kitengela", "+254712345678")
        val SHOP_2 = Shop("shop_2", "MagTech Shop 2 (Deliverance Rd)", "Deliverance Road, Kitengela", "+254787654321")
        val ALL_SHOPS = Shop("all", "Combined Shops", "Chairman Rd & Deliverance Rd, Kitengela", "+254700000000")
        val LIST = listOf(SHOP_1, SHOP_2)
    }
}

data class UserSession(
    val userId: String,
    val email: String,
    val fullName: String,
    val shopId: String,
    val token: String,
    val isLoggedIn: Boolean = true
)

data class Customer(
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val idNumber: String,
    val phone: String,
    val shopId: String,
    val createdAt: String = System.currentTimeMillis().toString()
)

enum class LoanStatus {
    ACTIVE,
    PARTIALLY_PAID,
    PAID,
    EXTENDED,
    OVERDUE,
    DEFAULTED,
    CLOSED
}

data class Loan(
    val id: String = UUID.randomUUID().toString(),
    val loanNumber: String = "L${(10000..99999).random()}",
    val customerId: String,
    val customerName: String,
    val customerPhone: String,
    val customerIdNumber: String = "",
    val shopId: String,
    val loanAmount: Double,
    val amountPayable: Double,
    val totalPaid: Double = 0.0,
    val balancePayable: Double = amountPayable - totalPaid,
    val startDate: String,
    val dueDate: String,
    val status: LoanStatus = if (balancePayable <= 0) LoanStatus.PAID else LoanStatus.ACTIVE,
    val notes: String = "",
    val collateralItems: List<LoanItem> = emptyList(),
    val createdAt: String = System.currentTimeMillis().toString()
)

data class LoanItem(
    val id: String = UUID.randomUUID().toString(),
    val loanId: String = "",
    val category: String, // Phones, Laptops, TVs, Audio, Fridges, Cookers, Home Appliances, Kitchen, Gaming, Accessories, Other
    val itemName: String,
    val description: String = "",
    val condition: String, // LIKE NEW, GOOD, FAIR
    val photoUrls: List<String> = emptyList()
)

data class LoanPayment(
    val id: String = UUID.randomUUID().toString(),
    val loanId: String,
    val shopId: String,
    val amount: Double,
    val paymentDate: String = System.currentTimeMillis().toString(),
    val paymentMethod: String = "M-PESA",
    val previousBalance: Double,
    val newBalance: Double,
    val receivedBy: String
)

data class LoanRenewal(
    val id: String = UUID.randomUUID().toString(),
    val loanId: String,
    val shopId: String,
    val renewalNumber: Int = 1,
    val feePaid: Double,
    val oldDueDate: String,
    val newDueDate: String,
    val renewalDate: String = System.currentTimeMillis().toString()
)

data class Product(
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val category: String,
    val condition: String, // LIKE NEW, GOOD, FAIR
    val price: Double,
    val source: String, // LOAN, PURCHASE, CONSIGNMENT, DIRECT
    val sourceId: String = "",
    val shopId: String,
    val status: String = "AVAILABLE", // AVAILABLE, RESERVED, SOLD
    val description: String = "",
    val isMarketplaceVisible: Boolean = true,
    val photoUrls: List<String> = emptyList(),
    val createdAt: String = System.currentTimeMillis().toString()
)

data class PurchaseRecord(
    val id: String = UUID.randomUUID().toString(),
    val itemName: String,
    val category: String,
    val purchaseAmount: Double,
    val sellerName: String,
    val sellerPhone: String,
    val sellerIdNumber: String = "",
    val shopId: String,
    val createdAt: String = System.currentTimeMillis().toString()
)

data class ConsignmentRecord(
    val id: String = UUID.randomUUID().toString(),
    val itemName: String,
    val category: String,
    val agreedPrice: Double,
    val commissionRate: Double = 10.0,
    val ownerName: String,
    val ownerPhone: String,
    val shopId: String,
    val status: String = "ACTIVE",
    val createdAt: String = System.currentTimeMillis().toString()
)

data class SaleRecord(
    val id: String = UUID.randomUUID().toString(),
    val productId: String,
    val productName: String,
    val saleAmount: Double,
    val buyerName: String = "Walk-in Buyer",
    val buyerPhone: String = "",
    val paymentMethod: String = "M-PESA",
    val shopId: String,
    val soldBy: String = "Admin",
    val saleDate: String = System.currentTimeMillis().toString()
)

data class TransactionRecord(
    val id: String = UUID.randomUUID().toString(),
    val type: String, // LOAN_CREATED, PAYMENT_RECEIVED, RENEWAL_EXTENDED, PRODUCT_PURCHASED, PRODUCT_SOLD, CONSIGNMENT_RECEIVED
    val title: String,
    val amount: Double,
    val shopId: String,
    val referenceId: String = "",
    val referenceType: String = "",
    val detailsJson: String = "",
    val createdAt: String = System.currentTimeMillis().toString()
)
