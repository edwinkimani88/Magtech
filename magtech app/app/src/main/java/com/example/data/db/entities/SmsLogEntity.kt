package com.example.data.db.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "sms_logs")
data class SmsLogEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val customerId: Long,
    val phoneNumber: String,
    val messageText: String,
    val sentTimestamp: Long = System.currentTimeMillis(),
    val status: String = "SENT" // "SENT", "SIMULATED", "FAILED"
)
