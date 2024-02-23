// FinanceDao.kt
package com.example.myapplication.android

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate


@Dao
interface FinanceDao {
    @Query("SELECT EXISTS(SELECT 1 FROM finance WHERE date = :date)")
    suspend fun exists(date: LocalDate): Boolean

    // Method to insert a new finance entry
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(finance: Finance)

    @Query("SELECT * FROM finance")
    fun getAll(): List<Finance>

    @Insert
    fun insertAll(vararg finances: Finance)

    @Query("UPDATE finance SET expectedIncome = :income, heldFund = 0.0, expectedExpenditure = :expenditure, customNotes = :notes WHERE date = :date")
    fun updateDayDetails(date: LocalDate, income: Double, expenditure: Double, notes: String): Int

    @Query("SELECT * FROM finance WHERE date BETWEEN :startDate AND :endDate")
    fun getFinanceDataBetweenDates(startDate: LocalDate, endDate: LocalDate): List<Finance>
}
