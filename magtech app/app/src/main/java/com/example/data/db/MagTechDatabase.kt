package com.example.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.data.db.daos.*
import com.example.data.db.entities.*
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
    version = 2,
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
                    .addCallback(MagTechDatabaseCallback(scope))
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }

    private class MagTechDatabaseCallback(
        private val scope: CoroutineScope
    ) : RoomDatabase.Callback() {
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            INSTANCE?.let { database ->
                scope.launch(Dispatchers.IO) {
                    populateInitialNairobiData(database)
                }
            }
        }

        suspend fun populateInitialNairobiData(db: MagTechDatabase) {
            val customerDao = db.customerDao()
            val itemDao = db.itemDao()
            val loanDao = db.loanDao()
            val transactionDao = db.transactionDao()

            val now = System.currentTimeMillis()
            val dayMs = 24 * 60 * 60 * 1000L

            // 1. Sample Customers
            val c1Id = customerDao.insertCustomer(
                CustomerEntity(
                    fullName = "Brian Omondi Onyango",
                    nationalId = "34892104",
                    phoneNumber = "0722123456",
                    notes = "Regular customer, very reliable. Works in Westlands."
                )
            )
            val c2Id = customerDao.insertCustomer(
                CustomerEntity(
                    fullName = "Kevin Kiprop Chebet",
                    nationalId = "31908234",
                    phoneNumber = "0711987654",
                    notes = "Left HP Laptop as collateral."
                )
            )
            val c3Id = customerDao.insertCustomer(
                CustomerEntity(
                    fullName = "Wanjiku Njuguna",
                    nationalId = "28901233",
                    phoneNumber = "0790554433",
                    notes = "Sold Sony TV outright."
                )
            )

            // 2. Sample Items across Shop 1 & Shop 2
            val item1Id = itemDao.insertItem(
                ItemEntity(
                    itemName = "Samsung Galaxy S23 Ultra (256GB)",
                    category = "Phones",
                    brand = "Samsung",
                    condition = "Like New",
                    estimatedMarketValue = 85000.0,
                    forcedSaleValue = 60000.0,
                    notes = "Phantom Black, clean screen, includes original charger.",
                    photoUrlsJson = "sample_s23_1,sample_s23_2",
                    status = "Active Loan",
                    entryType = "LOAN",
                    isPublishedToMarketplace = true,
                    marketplacePrice = 75000.0,
                    customerId = c1Id,
                    shopLocation = "Shop 1",
                    createdAt = now - (5 * dayMs)
                )
            )

            val item2Id = itemDao.insertItem(
                ItemEntity(
                    itemName = "HP EliteBook 840 G6 (Core i7, 16GB, 512GB SSD)",
                    category = "Laptops",
                    brand = "HP",
                    condition = "Good",
                    estimatedMarketValue = 48000.0,
                    forcedSaleValue = 35000.0,
                    notes = "Silver casing, keyboard light active, long battery life.",
                    photoUrlsJson = "sample_hp_1,sample_hp_2",
                    status = "Active Loan",
                    entryType = "LOAN",
                    isPublishedToMarketplace = true,
                    marketplacePrice = 42000.0,
                    customerId = c2Id,
                    shopLocation = "Shop 1",
                    createdAt = now - (10 * dayMs)
                )
            )

            val item3Id = itemDao.insertItem(
                ItemEntity(
                    itemName = "Sony Bravia 55\" 4K Smart Android TV",
                    category = "TVs & Audio",
                    brand = "Sony",
                    condition = "Like New",
                    estimatedMarketValue = 65000.0,
                    forcedSaleValue = 45000.0,
                    notes = "Bought directly from seller Wanjiku.",
                    photoUrlsJson = "sample_tv_1,sample_tv_2",
                    status = "Purchased",
                    entryType = "DIRECT_PURCHASE",
                    isPublishedToMarketplace = true,
                    marketplacePrice = 58000.0,
                    customerId = c3Id,
                    shopLocation = "Shop 2",
                    createdAt = now - (2 * dayMs)
                )
            )

            val item4Id = itemDao.insertItem(
                ItemEntity(
                    itemName = "PlayStation 5 Digital Edition (Slim)",
                    category = "Gaming",
                    brand = "Sony",
                    condition = "Like New",
                    estimatedMarketValue = 72000.0,
                    forcedSaleValue = 52000.0,
                    notes = "2 DualSense Wireless Controllers included.",
                    photoUrlsJson = "sample_ps5_1,sample_ps5_2",
                    status = "Listed",
                    entryType = "DIRECT_PURCHASE",
                    isPublishedToMarketplace = true,
                    marketplacePrice = 68000.0,
                    customerId = null,
                    shopLocation = "Shop 2",
                    createdAt = now - (1 * dayMs)
                )
            )

            // 3. Sample Loans
            loanDao.insertLoan(
                LoanEntity(
                    itemId = item1Id,
                    customerId = c1Id,
                    amountGiven = 35000.0,
                    totalPayable = 40000.0,
                    paidAmount = 10000.0,
                    dateIssued = now - (5 * dayMs),
                    dueDate = now + (3 * dayMs),
                    status = "ACTIVE",
                    shopLocation = "Shop 1"
                )
            )

            loanDao.insertLoan(
                LoanEntity(
                    itemId = item2Id,
                    customerId = c2Id,
                    amountGiven = 22000.0,
                    totalPayable = 26000.0,
                    paidAmount = 0.0,
                    dateIssued = now - (10 * dayMs),
                    dueDate = now, // Due Today!
                    status = "DUE_TODAY",
                    shopLocation = "Shop 1"
                )
            )

            // 4. Sample Transactions
            transactionDao.insertTransaction(
                TransactionEntity(
                    type = "LOAN_DISBURSED",
                    amount = 35000.0,
                    itemId = item1Id,
                    customerId = c1Id,
                    description = "Loan disbursed for Samsung Galaxy S23 Ultra",
                    shopLocation = "Shop 1",
                    timestamp = now - (5 * dayMs)
                )
            )
            transactionDao.insertTransaction(
                TransactionEntity(
                    type = "LOAN_REPAYMENT",
                    amount = 10000.0,
                    itemId = item1Id,
                    customerId = c1Id,
                    description = "Partial loan payment by Brian Omondi",
                    shopLocation = "Shop 1",
                    timestamp = now - (2 * dayMs)
                )
            )
            transactionDao.insertTransaction(
                TransactionEntity(
                    type = "DIRECT_PURCHASE",
                    amount = 45000.0,
                    itemId = item3Id,
                    customerId = c3Id,
                    description = "Direct buy: Sony Bravia 55'' TV",
                    shopLocation = "Shop 2",
                    timestamp = now - (2 * dayMs)
                )
            )
        }
    }
}
