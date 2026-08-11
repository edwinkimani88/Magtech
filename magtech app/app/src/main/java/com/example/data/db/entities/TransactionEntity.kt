package com.example.data.db.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "transactions")
data class TransactionEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val type: String, // "LOAN_DISBURSED", "LOAN_REPAYMENT", "DIRECT_PURCHASE", "MARKETPLACE_SALE"
    val amount: Double,
    val itemId: Long? = null,
    val customerId: Long? = null,
    val description: String,
    val shopLocation: String = "Shop 1", // "Shop 1" or "Shop 2"
    val timestamp: Long = System.currentTimeMillis()
)
