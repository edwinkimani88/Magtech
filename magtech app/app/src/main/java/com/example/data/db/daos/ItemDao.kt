package com.example.data.db.daos

import androidx.room.*
import com.example.data.db.entities.ItemEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ItemDao {
    @Query("SELECT * FROM items ORDER BY createdAt DESC")
    fun getAllItems(): Flow<List<ItemEntity>>

    @Query("SELECT * FROM items WHERE isPublishedToMarketplace = 1 ORDER BY createdAt DESC")
    fun getMarketplaceItems(): Flow<List<ItemEntity>>

    @Query("SELECT * FROM items WHERE id = :id")
    suspend fun getItemById(id: Long): ItemEntity?

    @Query("SELECT * FROM items WHERE customerId = :customerId ORDER BY createdAt DESC")
    fun getItemsForCustomer(customerId: Long): Flow<List<ItemEntity>>

    @Query("SELECT COUNT(*) FROM items")
    fun getTotalItemCount(): Flow<Int>

    @Query("SELECT COUNT(*) FROM items WHERE isPublishedToMarketplace = 1")
    fun getMarketplaceCount(): Flow<Int>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertItem(item: ItemEntity): Long

    @Update
    suspend fun updateItem(item: ItemEntity)

    @Delete
    suspend fun deleteItem(item: ItemEntity)
}
