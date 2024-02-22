
package com.example.myapplication.android

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.LocalDate

//View Model
class MainViewModel(private val financeDao: FinanceDao) : ViewModel() {

    // Use LiveData to expose selectedDate if observing in activities/fragments
    private val _selectedDate = MutableLiveData(LocalDate.now())
    val selectedDate: LiveData<LocalDate> = _selectedDate

    // MutableStateFlow for selected time period with default value
    private val _selectedTimePeriod = MutableStateFlow(TimePeriod.Day)
    val selectedTimePeriod: StateFlow<TimePeriod> = _selectedTimePeriod.asStateFlow()

    // Assuming data type for fetched data
    private val _financialData = MutableStateFlow<List<Finance>>(emptyList())
    val financialData: StateFlow<List<Finance>> = _financialData.asStateFlow()

    init {
        fetchDataBasedOnTimePeriod(TimePeriod.Day)
    }

    // Or use State for Compose
    var selectedDateState: LocalDate by mutableStateOf(LocalDate.now())
        private set

    var currentPageState by mutableStateOf("Main")
        private set

    fun navigateTo(page: String) {
        currentPageState = page
    }

    fun setNewDate(newDate: LocalDate) {
        selectedDateState = newDate
    }

    fun ensureDataForDate(date: LocalDate) = viewModelScope.launch {
        val exists = financeDao.exists(date) // Assume exists is a method that checks if the date exists
        if (!exists) {
            populateDatesStartingFrom(date)
        }
    }

    private suspend fun populateDatesStartingFrom(date: LocalDate) {
        for (i in 0..31) { // Populate for the requested date + the next 6 days
            val newDate = date.plusDays(i.toLong())
            val newFinanceEntry = Finance(newDate, 0.0, 0.0, 0.0, "")
            financeDao.insert(newFinanceEntry)
        }
    }

    // Asynchronous update function using Kotlin coroutines
    fun updateFinance(date: LocalDate, income: Double, expenditure: Double, notes: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                Log.d("ViewModel", "Updating finance for $date")
                val updatedRows = financeDao.updateDayDetails(date, income, expenditure, notes)
                Log.d("ViewModel", "Finance updated successfully")

                if (updatedRows > 0) {
                    // If rows were updated, fetch and log all entries to verify.
                    val allFinances = financeDao.getAll() // Assuming getAll returns a list of all Finance entries
                    allFinances.forEach { finance ->
                        Log.d("DatabaseContent", finance.toString())
                    }
                } else {
                    Log.d("ViewModel", "No rows were updated, check your query or the state of the database.")
                }
            } catch (e: Exception) {
                Log.e("ViewModel", "Exception in ViewModel", e)
            }
        }
    }

    fun updateSelectedTimePeriod(timePeriod: TimePeriod) {
        _selectedTimePeriod.value = timePeriod
    }

    private fun fetchDataBasedOnTimePeriod(timePeriod: TimePeriod) {
        viewModelScope.launch(Dispatchers.IO) {
            val startDate = LocalDate.now()
            val endDate = when (timePeriod) {
                TimePeriod.Day -> startDate
                TimePeriod.Week -> startDate.plusWeeks(1)
                TimePeriod.Month -> startDate.plusMonths(1)
            }
            ensureDataForDate(startDate)
            val data = financeDao.getFinanceDataBetweenDates(startDate, endDate)
            withContext(Dispatchers.Main) {
                _financialData.value = data
            }
        }
    }

    fun hasNetDeficit(date: LocalDate): Boolean {
        val finance = financialData.value.find { it.date == date }
        return finance?.netIncome?.let { it < 0 } ?: false
    }

    fun hasNetSurplus(date: LocalDate): Boolean {
        val finance = financialData.value.find { it.date == date }
        return finance?.netIncome?.let { it > 0 } ?: false
    }

}