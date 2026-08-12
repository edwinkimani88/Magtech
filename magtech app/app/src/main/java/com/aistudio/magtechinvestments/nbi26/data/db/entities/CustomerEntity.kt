package com.aistudio.magtechinvestments.nbi26.data.db.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "customers")
data class CustomerEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val fullName: String,
    val nationalId: String,
    val phoneNumber: String,
    val notes: String = "",
    val createdAt: Long = System.currentTimeMillis()
)
