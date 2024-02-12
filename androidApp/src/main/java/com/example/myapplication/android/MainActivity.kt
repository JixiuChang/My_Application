package com.example.myapplication.android

import android.os.Bundle
import android.widget.Space
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.BlendMode.Companion.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.myapplication.Greeting
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.TextStyle
import java.util.Locale

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyApplicationTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    IndexView()
                }
            }
        }
    }
}

@Composable
fun IndexView() {
    var currentPageState by remember { mutableStateOf("Main") }
    when (currentPageState) {
        "Calendar" -> CalendarView { currentPageState = "Main" }
        "MasterManager" -> MasterManagerView { currentPageState = "Main" }
        "Main" -> MainView(
            onCalendarClick = { currentPageState = "Calendar" },
            onMasterManagerClick = { currentPageState = "MasterManager" }
        )
    }
}

@Composable
fun MainView(onCalendarClick: () -> Unit, onMasterManagerClick: () -> Unit) {
    Row(modifier = Modifier
        .fillMaxWidth()
        .padding(16.dp)) {
        Button(
            onClick = onCalendarClick,
            modifier = Modifier.padding(8.dp)
        ) {
            Icon(
                imageVector = Icons.Filled.DateRange,
                contentDescription = "Calendar"
            )
        }
        Spacer(modifier = Modifier.weight(1f))
        Button(
            onClick = onMasterManagerClick,
            modifier = Modifier.padding(8.dp)
        ) {
            Icon(
                imageVector = Icons.Filled.Edit,
                contentDescription = "Edit"
            )
        }
    }
}
@Composable
fun CalendarView(onClickBack: () -> Unit) {
    var selectedMonth by remember { mutableStateOf(YearMonth.now()) }
    val daysInMonth = remember(selectedMonth) { selectedMonth.lengthOfMonth() }
    val firstOfMonth = remember(selectedMonth) { selectedMonth.atDay(1) }
    val lastOfMonth = remember(selectedMonth) { selectedMonth.atEndOfMonth() }
    val startDayOfWeek = firstOfMonth.dayOfWeek.value
    val days = remember(selectedMonth) {
        val totalDays = List(daysInMonth + startDayOfWeek - 1) { dayIndex ->
            if (dayIndex >= startDayOfWeek) {
                LocalDate.of(selectedMonth.year, selectedMonth.month, dayIndex - startDayOfWeek + 1)
            } else null
        }
        // Fill in the leading and trailing nulls to complete the week
        val additionalDaysAtStart = startDayOfWeek - 1
        val additionalDaysAtEnd = 7 - (totalDays.size % 7)
        List(additionalDaysAtStart) { null } + totalDays + List(additionalDaysAtEnd) { null }
    }

    Column(modifier = Modifier
        .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Row(modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)) {
            Button(
                onClick = { onClickBack() },
                modifier = Modifier.padding(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Filled.Home,
                    contentDescription = "Main Menu"
                )
            }
        }

        MonthYearHeader(selectedMonth) {
            selectedMonth = it
        }
        Spacer(modifier = Modifier.height(8.dp))
        DaysOfWeekHeader()
        Spacer(modifier = Modifier.height(8.dp))
        CalendarDaysGrid(days)
    }
}


@Composable
fun MonthYearHeader(selectedMonth: YearMonth, onMonthChange: (YearMonth) -> Unit) {
    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.Center, modifier = Modifier.fillMaxWidth()) {
        IconButton(onClick = { onMonthChange(selectedMonth.minusMonths(1)) }) {
            Icon(imageVector = Icons.Filled.KeyboardArrowLeft, contentDescription = "Previous Month")
        }
        Text("${selectedMonth.month.getDisplayName(TextStyle.FULL, Locale.getDefault())} ${selectedMonth.year}", style = MaterialTheme.typography.titleMedium)
        IconButton(onClick = { onMonthChange(selectedMonth.plusMonths(1)) }) {
            Icon(imageVector = Icons.Filled.KeyboardArrowRight, contentDescription = "Next Month")
        }
    }
}

@Composable
fun DaysOfWeekHeader() {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        DayOfWeek.values().forEach { dayOfWeek ->
            Text(text = dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.getDefault()), modifier = Modifier.weight(1f), textAlign = TextAlign.Center)
        }
    }
}

@Composable
fun CalendarDaysGrid(days: List<LocalDate?>) {
    LazyVerticalGrid(columns = GridCells.Fixed(7), contentPadding = PaddingValues(4.dp), modifier = Modifier.fillMaxWidth()) {
        items(days) { day ->
            Box(modifier = Modifier
                .padding(4.dp)
                .aspectRatio(1f), contentAlignment = Alignment.Center) {
                if (day != null) {
                    Text(text = day.dayOfMonth.toString(), style = MaterialTheme.typography.bodyMedium)
                }
            }
        }
    }
}

@Composable
fun MasterManagerView(onClickBack: () -> Unit) {
    Row(modifier = Modifier
        .fillMaxWidth()
        .padding(16.dp)) {
        Button(
            onClick = { onClickBack() },
            modifier = Modifier.padding(8.dp)
        ) {
            Icon(
                imageVector = Icons.Filled.Home,
                contentDescription = "Main Menu"
            )
        }
    }
}

@Preview
@Composable
fun DefaultPreview() {
    MyApplicationTheme {
        IndexView()
    }
}
