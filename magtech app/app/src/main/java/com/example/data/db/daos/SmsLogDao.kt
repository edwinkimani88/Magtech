package com.example.data.db.daos

import androidx.room.*
import com.example.data.db.entities.SmsLogEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SmsLogDao {
    @Query("SELECT * FROM sms_logs ORDER BY sentTimestamp DESC")
    fun getAllSmsLogs(): Flow<List<SmsLogEntity>>

    @Query("SELECT * FROM sms_logs WHERE customerId = :customerId ORDER BY sentTimestamp DESC")
    fun getSmsLogsForCustomer(customerId: Long): Flow<List<SmsLogEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSmsLog(smsLog: SmsLogEntity): Long
}
