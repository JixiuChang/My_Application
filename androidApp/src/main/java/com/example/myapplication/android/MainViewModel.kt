
package com.example.myapplication.android

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
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

    // Asynchronous update function using Kotlin coroutines
    fun updateFinance(date: LocalDate, income: Double, expenditure: Double, notes: String) {
        viewModelScope.launch {
            try {
                Log.d("ViewModel", "Updating finance for $date")
                financeDao.updateDayDetails(date, income, expenditure, notes)
                Log.d("ViewModel", "Finance updated successfully")
            } catch (e: Exception) {
                Log.e("ViewModel", "Exception in ViewModel", e)
            }
        }
    }

    fun updateSelectedTimePeriod(timePeriod: TimePeriod) {
        _selectedTimePeriod.value = timePeriod
    }

    private fun fetchDataBasedOnTimePeriod(timePeriod: TimePeriod) {
        viewModelScope.launch {
            val startDate = LocalDate.now()
            val endDate = when (timePeriod) {
                TimePeriod.Day -> startDate
                TimePeriod.Week -> startDate.plusWeeks(1)
                TimePeriod.Month -> startDate.plusMonths(1)
            }
            val data = financeDao.getFinanceDataBetweenDates(startDate, endDate)
            _financialData.value = data
        }
    }
}