package com.aistudio.magtechinvestments.nbi26.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.aistudio.magtechinvestments.nbi26.data.db.daos.*
import com.aistudio.magtechinvestments.nbi26.data.db.entities.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        ItemEntity::class,
        CustomerEntity::class,
        LoanEntity::class,
        SmsLogEntity::class,
        TransactionEntity::class
    ],
    version = 3,
    exportSchema = false
)
abstract class MagTechDatabase : RoomDatabase() {
    abstract fun itemDao(): ItemDao
    abstract fun customerDao(): CustomerDao
    abstract fun loanDao(): LoanDao
    abstract fun smsLogDao(): SmsLogDao
    abstract fun transactionDao(): TransactionDao

    companion object {
        @Volatile
        private var INSTANCE: MagTechDatabase? = null

        fun getDatabase(context: Context, scope: CoroutineScope): MagTechDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    MagTechDatabase::class.java,
                    "magtech_investments_db"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
