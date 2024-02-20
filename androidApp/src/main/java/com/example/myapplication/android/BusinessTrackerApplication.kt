package com.example.myapplication.android

import android.app.Application
import androidx.room.Room

class BusinessTrackerApplication : Application() {
    lateinit var database: AppDatabase
    lateinit var financeDao: FinanceDao

    override fun onCreate() {
        super.onCreate()
        // Initialize your Room database
        database =
            Room.databaseBuilder(applicationContext, AppDatabase::class.java, "finance_database")
                .build()
        financeDao = database.financeDao()
    }
}