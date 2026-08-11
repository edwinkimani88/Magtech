package com.example.data.db.daos

import androidx.room.*
import com.example.data.db.entities.LoanEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface LoanDao {
    @Query("SELECT * FROM loans ORDER BY dateIssued DESC")
    fun getAllLoans(): Flow<List<LoanEntity>>

    @Query("SELECT * FROM loans WHERE id = :id")
    suspend fun getLoanById(id: Long): LoanEntity?

    @Query("SELECT * FROM loans WHERE customerId = :customerId ORDER BY dateIssued DESC")
    fun getLoansForCustomer(customerId: Long): Flow<List<LoanEntity>>

    @Query("SELECT * FROM loans WHERE itemId = :itemId LIMIT 1")
    suspend fun getLoanForItem(itemId: Long): LoanEntity?

    @Query("SELECT COUNT(*) FROM loans WHERE status = 'ACTIVE' OR status = 'DUE_TODAY' OR status = 'OVERDUE'")
    fun getActiveLoanCount(): Flow<Int>

    @Query("SELECT * FROM loans WHERE status = 'DUE_TODAY' OR (dueDate >= :todayStart AND dueDate <= :todayEnd AND status != 'REDEEMED')")
    fun getLoansDueToday(todayStart: Long, todayEnd: Long): Flow<List<LoanEntity>>

    @Query("SELECT * FROM loans WHERE dueDate < :currentTime AND status != 'REDEEMED' AND status != 'DEFAULTED'")
    fun getOverdueLoans(currentTime: Long): Flow<List<LoanEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLoan(loan: LoanEntity): Long

    @Update
    suspend fun updateLoan(loan: LoanEntity)
}
