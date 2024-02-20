
package com.example.myapplication.android

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import java.time.LocalDate

//View Model
class MainViewModel(private val financeDao: FinanceDao) : ViewModel() {
    // Use LiveData to expose selectedDate if observing in activities/fragments
    private val _selectedDate = MutableLiveData(LocalDate.now())
    val selectedDate: LiveData<LocalDate> = _selectedDate

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
}