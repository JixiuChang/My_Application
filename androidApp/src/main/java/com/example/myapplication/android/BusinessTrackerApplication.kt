package com.example.myapplication.android

import android.app.Application
import android.util.Log
import androidx.room.Room

class BusinessTrackerApplication : Application() {
    lateinit var database: AppDatabase
    lateinit var financeDao: FinanceDao

    override fun onCreate() {
        super.onCreate()
        Log.d(this.javaClass.simpleName, "onCreate")
        // Initialize your Room database
        database =
            Room.databaseBuilder(applicationContext, AppDatabase::class.java, "finance_database")
                .build()
        financeDao = database.financeDao()
    }
}