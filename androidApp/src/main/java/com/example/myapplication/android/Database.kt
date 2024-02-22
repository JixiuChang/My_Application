// AppDatabase.kt
package com.example.myapplication.android

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.LocalDate

@Database(entities = [Finance::class], version = 1)
@TypeConverters(LocalDateConverter::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun financeDao(): FinanceDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "finance_database"
                )
                    .addCallback(roomDatabaseCallback) // Add the callback here
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }

        // Define the callback
        private val roomDatabaseCallback = object : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                INSTANCE?.let { database ->
                    CoroutineScope(Dispatchers.IO).launch {
                        populateDatabase(database.financeDao())
                    }
                }
            }
        }

        // Populate the database with initial data
        suspend fun populateDatabase(financeDao: FinanceDao) {
            // Here you can define your prepopulation logic
            val startDate = LocalDate.now()
            for (i in 0 until 7) { // Example: Prepopulate for the next 365 days
                val date = startDate.plusDays(i.toLong())
                val finance = Finance(date, 0.0, 0.0, 0.0, "")
                financeDao.insert(finance)
            }
        }
    }
}