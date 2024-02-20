// FinanceDao.kt
package com.example.myapplication.android

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import java.time.LocalDate


@Dao
interface FinanceDao {
    @Query("SELECT * FROM finance")
    fun getAll(): List<Finance>

    @Insert
    fun insertAll(vararg finances: Finance)

    @Query("UPDATE finance SET expectedIncome = :income, expectedExpenditure = :expenditure, customNotes = :notes WHERE date = :date")
    fun updateDayDetails(date: LocalDate, income: Double, expenditure: Double, notes: String)
}
