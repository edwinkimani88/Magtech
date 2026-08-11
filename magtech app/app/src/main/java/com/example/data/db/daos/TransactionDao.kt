package com.example.data.db.daos

import androidx.room.*
import com.example.data.db.entities.TransactionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TransactionDao {
    @Query("SELECT * FROM transactions ORDER BY timestamp DESC")
    fun getAllTransactions(): Flow<List<TransactionEntity>>

    @Query("SELECT SUM(amount) FROM transactions WHERE type = 'LOAN_REPAYMENT' OR type = 'MARKETPLACE_SALE'")
    fun getTotalRevenue(): Flow<Double?>

    @Query("SELECT SUM(amount) FROM transactions WHERE type = 'LOAN_DISBURSED'")
    fun getTotalLoansDisbursed(): Flow<Double?>

    @Query("SELECT SUM(amount) FROM transactions WHERE type = 'DIRECT_PURCHASE'")
    fun getTotalDirectPurchases(): Flow<Double?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTransaction(transaction: TransactionEntity): Long
}
