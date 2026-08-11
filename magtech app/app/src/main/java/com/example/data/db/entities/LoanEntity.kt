package com.example.data.db.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "loans")
data class LoanEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val itemId: Long,
    val customerId: Long,
    val amountGiven: Double,
    val totalPayable: Double,
    val paidAmount: Double = 0.0,
    val dateIssued: Long,
    val dueDate: Long,
    val status: String, // "ACTIVE", "DUE_TODAY", "OVERDUE", "REDEEMED", "DEFAULTED"
    val shopLocation: String = "Shop 1", // "Shop 1" or "Shop 2"
    val createdAt: Long = System.currentTimeMillis()
)
