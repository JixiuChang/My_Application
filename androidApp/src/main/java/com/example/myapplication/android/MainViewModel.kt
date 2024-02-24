
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
        fetchDataBasedOnTimePeriod(TimePeriod.Day, LocalDate.now())
    }

    var databaseVersion: Int by mutableStateOf(0)
        private set

    // Or use State for Compose
    var selectedDateState: LocalDate by mutableStateOf(LocalDate.now())
        private set

    var currentPageState by mutableStateOf("Main")
        private set

    private val _netExpectedIncome = MutableStateFlow(0.0)
    val netExpectedIncome: StateFlow<Double> = _netExpectedIncome.asStateFlow()

    private val _currentlyHeldFunding = MutableStateFlow(0.0)
    val currentlyHeldFunding: StateFlow<Double> = _currentlyHeldFunding.asStateFlow()

    private val _netExpectedExpenditure = MutableStateFlow(0.0)
    val netExpectedExpenditure: StateFlow<Double> = _netExpectedExpenditure.asStateFlow()

    private val _expectedHeldFundAfter = MutableStateFlow(0.0)
    val expectedHeldFundAfter: StateFlow<Double> = _expectedHeldFundAfter.asStateFlow()

    fun navigateTo(page: String) {
        currentPageState = page
    }

    fun setNewDate(newDate: LocalDate) {
        selectedDateState = newDate
        //databaseVersion ++
        fetchDataBasedOnTimePeriod(TimePeriod.Day, newDate)
    }

    fun ensureDataForDate(date: LocalDate) = viewModelScope.launch {
        for (i in 0..31) { // Populate for the requested date + the next 31 days
            val newDate = date.plusDays(i.toLong())
            val exists = financeDao.exists(newDate)
            if (!exists) {
                val newFinanceEntry = Finance(newDate, 0.0, 0.0, 0.0, "")
                val inserted = financeDao.insert(newFinanceEntry)
                Log.d("Inserted", "$inserted")
            }
        }
    }

    // Asynchronous update function using Kotlin coroutines
    fun updateFinance(date: LocalDate, income: Double, expenditure: Double, notes: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                ensureDataForDate(date)
                Log.d("ViewModel", "Updating finance for $date")
                financeDao.updateDayDetails(date, income, expenditure, notes)
                fetchDataBasedOnTimePeriod(selectedTimePeriod.value, date)
                Log.d("ViewModel", financeDao.getFinanceDataBetweenDates(date, date).toString())
                Log.d("ViewModel", "Finance updated successfully")
                databaseVersion ++
            } catch (e: Exception) {
                Log.e("ViewModel", "Exception in ViewModel", e)
            }
        }
    }

    fun updateSelectedTimePeriod(timePeriod: TimePeriod) {
        _selectedTimePeriod.value = timePeriod
    }

    private fun fetchDataBasedOnTimePeriod(timePeriod: TimePeriod, startDate: LocalDate) {
        viewModelScope.launch(Dispatchers.IO) {
            //val startDate = selectedDateState// LocalDate.now()
            val endDate = when (timePeriod) {
                TimePeriod.Day -> startDate
                TimePeriod.Week -> startDate.plusWeeks(1)
                TimePeriod.Month -> startDate.plusMonths(1)
            }
            ensureDataForDate(startDate)
            val data = financeDao.getFinanceDataBetweenDates(startDate, endDate).first()
            val prevDate = startDate.minusDays(1)
            val prevData = financeDao.getFinanceDataBetweenDates(prevDate, prevDate).first()
            withContext(Dispatchers.Main) {
                _financialData.value = listOf(data)

                // Aggregate the sums of each financial metric
                _netExpectedIncome.value = data.expectedIncome //  data.sumOf { it.expectedIncome }
                _netExpectedExpenditure.value = data.expectedExpenditure // data.sumOf { it.expectedExpenditure }

                // TODO: add accumulation of all the previous days.
                _currentlyHeldFunding.value = prevData.expectedIncome - prevData.expectedExpenditure
                _expectedHeldFundAfter.value = _netExpectedIncome.value - _netExpectedExpenditure.value + _currentlyHeldFunding.value

            }
            // a, b, c
            // b = b(-1) + a(-1) - c(-1)
        }
    }

    suspend fun hasNetDeficit(date: LocalDate): Boolean = withContext(Dispatchers.IO) {
        val data = financeDao.getFinanceDataBetweenDates(date, date)
        if (data.isNotEmpty()) {
            val firstEntry = data.first()
            firstEntry.netIncome < 0
        } else {
            false
        }
    }

    suspend fun hasNetSurplus(date: LocalDate): Boolean = withContext(Dispatchers.IO) {
        val data = financeDao.getFinanceDataBetweenDates(date, date)
        if (data.isNotEmpty()) {
            val firstEntry = data.first()
            firstEntry.netIncome > 0
        } else {
            false
        }
    }


}