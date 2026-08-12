package com.aistudio.magtechinvestments.nbi26.data.db.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "items")
data class ItemEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val itemName: String,
    val category: String, // "Phones", "Laptops", "TVs & Audio", "Gaming", "Appliances"
    val brand: String,
    val condition: String, // "Like New", "Good", "Fair"
    val estimatedMarketValue: Double,
    val forcedSaleValue: Double,
    val notes: String = "",
    val photoUrlsJson: String = "", // Comma-separated URIs or paths (2 to 4 photos)
    val status: String, // "Active Loan", "Redeemed", "Purchased", "Listed", "Sold", "Disposed"
    val entryType: String, // "LOAN" or "DIRECT_PURCHASE"
    val isPublishedToMarketplace: Boolean = true,
    val marketplacePrice: Double = 0.0,
    val customerId: Long? = null,
    val shopLocation: String = "Shop 1", // "Shop 1" or "Shop 2"
    val createdAt: Long = System.currentTimeMillis()
)
