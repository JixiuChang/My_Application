package com.example.myapplication.android

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.unit.dp
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.sp
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Locale
import androidx.room.*
data class ProgressBarSection(
    val value: Float,
    val color: Color
)

enum class TimePeriod {
    Day, Week, Month
}

// DateConverter used by Database
class LocalDateConverter {
    @TypeConverter
    fun fromLocalDate(date: LocalDate): String {
        return date.toString()
    }

    @TypeConverter
    fun toLocalDate(dateString: String): LocalDate {
        return LocalDate.parse(dateString)
    }
}


//Database
@Entity(tableName = "finance")
data class Finance(
    @PrimaryKey val date: LocalDate,
    var expectedIncome: Double,
    var heldFund: Double,
    var expectedExpenditure: Double,
    var customNotes: String
) {
    val netIncome: Double
        get() = expectedIncome - expectedExpenditure
}

//Main Activity
class MainActivity : ComponentActivity() {
    private lateinit var viewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(javaClass.simpleName, "onCreate $application")
        viewModel = MainViewModel((application as BusinessTrackerApplication).financeDao)
        setContent {
            MyApplicationTheme(darkTheme = false) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    IndexView(viewModel)
                }
            }
            //Text("For Eyjafjalla")
        }
    }
}


//Index Views
@Composable
fun IndexView(viewModel: MainViewModel) {
    if (viewModel.currentPageState == "Main") {
        MainView(
            onCalendarClick = { viewModel.navigateTo("Calendar") },
            onMasterManagerClick = { viewModel.navigateTo("MasterManager") },
            viewModel = viewModel
            // Other parameters
        )
    } else if (viewModel.currentPageState == "Calendar") {
        CalendarView(
            onClickBack = { viewModel.navigateTo("Main") },
            viewModel = viewModel
        )
    } else if (viewModel.currentPageState == "MasterManager") {
        MasterManagerView(
            onClickBack = { viewModel.navigateTo("Main") },
            viewModel = viewModel
        )
    }
}

//Main View
@Composable
fun MainView(onCalendarClick: () -> Unit, onMasterManagerClick: () -> Unit, viewModel: MainViewModel) {
    val selectedTimePeriod = viewModel.selectedTimePeriod.collectAsState()

    LaunchedEffect(key1 = viewModel.databaseVersion) {
        Log.d(javaClass.simpleName,"database version ${viewModel.databaseVersion}")
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)  // Add padding to bottom to space from the progress bar
            ) {
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
            Spacer(modifier = Modifier.height(16.dp))
                Text(
                    "${viewModel.selectedDateState.format(DateTimeFormatter.ofPattern("MMM dd, yyyy"))}",
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .align(Alignment.CenterHorizontally),
                horizontalArrangement = Arrangement.SpaceEvenly,
            ) {
                Button(
                    onClick = { viewModel.updateSelectedTimePeriod(TimePeriod.Day) },
                    // Highlight the button if the time period is selected
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (selectedTimePeriod.value == TimePeriod.Day) Color.Gray else MaterialTheme.colorScheme.background
                    )
                ) {
                    Text("Day")
                }
                Button(
                    onClick = { viewModel.updateSelectedTimePeriod(TimePeriod.Week) },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (selectedTimePeriod.value == TimePeriod.Week) Color.Gray else MaterialTheme.colorScheme.background
                    )
                ) {
                    Text("Week")
                }
                Button(
                    onClick = { viewModel.updateSelectedTimePeriod(TimePeriod.Month) },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (selectedTimePeriod.value == TimePeriod.Month) Color.Gray else MaterialTheme.colorScheme.background
                    )
                ) {
                    Text("Month")
                }

            }
            // This Box will take up all available space, pushing the progress bar to the center
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f), // This will make the box take up all available space
                contentAlignment = Alignment.Center // This will align the progress bar in the center of the box
            ) {
                val netIncome = viewModel.netExpectedIncome.collectAsState().value.toFloat() - viewModel.netExpectedExpenditure.collectAsState().value.toFloat()
                val totalFloat = viewModel.expectedHeldFundAfter.collectAsState().value.toFloat()
                val netExpectedIncomePct: Float
                val netExpectedExpenditurePct: Float
                val leftoverSectionPct: Float

                if (netIncome > 0) {
                    val netExpectedIncome = viewModel.netExpectedIncome.collectAsState().value.toFloat() // Convert to Float
                    netExpectedIncomePct = netExpectedIncome / totalFloat
                    val leftoverSection = totalFloat - netExpectedIncome// Convert to Float
                    leftoverSectionPct = if (totalFloat > 0f) leftoverSection / totalFloat else 0f
                    netExpectedExpenditurePct = 0f
                } else {
                    val netExpectedExpenditure = viewModel.netExpectedExpenditure.collectAsState().value.toFloat() // Convert to Float
                    netExpectedExpenditurePct = netExpectedExpenditure / totalFloat
                    val leftoverSection = totalFloat - netExpectedExpenditure// Convert to Float
                    leftoverSectionPct = if (totalFloat > 0f) leftoverSection / totalFloat else 0f
                    netExpectedIncomePct = 0f
                }


                // Calculate percentages explicitly as Float


                PieChart(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(30.dp)
                    .padding(horizontal = 32.dp),  // Side paddings for the progress bar
                sections = listOf(
                    ProgressBarSection(
                        value = netExpectedExpenditurePct,
                        color = Color.Red
                    ),
                    ProgressBarSection(
                        value = leftoverSectionPct,
                        color = Color.Blue
                    ),
                    ProgressBarSection(
                        value = netExpectedIncomePct,
                        color = Color.Green
                    )
                )
            )
        }
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
            ) {
                // Display lines underneath the Pie Chart
                DisplayLine(label = "Net Expected Income", value = viewModel.netExpectedIncome.collectAsState().value, color = Color(0xFF66BB66))
                DisplayLine(label = "Currently Held Funding", value = viewModel.currentlyHeldFunding.collectAsState().value, color = Color.Transparent) // No square as per your request
                DisplayLine(label = "Net Expected Expenditure", value = viewModel.netExpectedExpenditure.collectAsState().value, color = Color(0xFFBB6666))
                DisplayLine(label = "Expected Held Fund After", value = viewModel.expectedHeldFundAfter.collectAsState().value, color = Color.LightGray)
            }
        }
    }
}


@Composable
fun DisplayLine(label: String, value: Double, color: Color) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(modifier = Modifier
            .size(16.dp)
            .background(color)
            .then(Modifier.padding(end = 8.dp)), contentAlignment = Alignment.Center) {
            // Conditionally display colored square, except when color is Transparent
            if (color != Color.Transparent) Spacer(Modifier.size(16.dp))
        }
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        ) {
            Row(
                modifier = Modifier.align(Alignment.CenterHorizontally)
            ) {
                Text(text = "$label: ${String.format("%.2f", value)}")
            }
        }
    }
}

@Composable
fun PieChart(
    modifier: Modifier,
    sections: List<ProgressBarSection>,
    backgroundColor: Color = Color(0xFFE0E0E0),
    spacing: Dp = 8.dp
) {
    Canvas(modifier = modifier) {
        // Calculate the total percentage to adjust sweep angles if they don't add up to 1 (100%)
        val totalPercentage = sections.sumOf { it.value.toDouble() }.toFloat()
        var startAngle = -90f // Start at the top (12 o'clock)

        // Draw the background circle
        val pieRadius = (size.minDimension - spacing.toPx() * 2) / 2 * 10
        val backgroundRadius = pieRadius + spacing.toPx()
        drawCircle(
            color = backgroundColor,
            radius = backgroundRadius, // Slightly larger than the pie chart
            center = center
        )

        // Draw each section of the pie chart
        sections.forEach { section ->
            val sweepAngle = (section.value / totalPercentage) * 360f // Calculate sweep angle
            val sectionColor = when (section.color) {
                Color.Red -> Color(0xFFBB6666) // Less vibrant red
                Color.Blue -> Color(0xFF8888CC)
                Color.Green -> Color(0xFF66BB66) // Less vibrant green
                else -> section.color // Other colors remain unchanged
            }

            drawArc(
                color = sectionColor,
                startAngle = startAngle,
                sweepAngle = sweepAngle,
                useCenter = true,
                topLeft = Offset(center.x - pieRadius, center.y - pieRadius),
                size = Size(pieRadius * 2, pieRadius * 2)
            )

            startAngle += sweepAngle
        }
    }
}

@Composable
fun CalendarView(onClickBack: () -> Unit, viewModel: MainViewModel) {

    // If you're using State in ViewModel
    val selectedDate = viewModel.selectedDateState
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        var selectedMonth by remember { mutableStateOf(YearMonth.from(selectedDate)) }
        val daysInMonth = remember(selectedMonth) { selectedMonth.lengthOfMonth() }
        val firstOfMonth = remember(selectedMonth) { selectedMonth.atDay(1) }
        val days = remember(selectedMonth) {
            val totalDays = mutableListOf<LocalDate?>()

            // Determine the correct offset for starting from Sunday
            val dayOffset = firstOfMonth.dayOfWeek.value % 7
            for (i in 1..dayOffset) {
                totalDays.add(null) // Add nulls for days before the first of the month
            }

            for (dayIndex in 1..daysInMonth) {
                totalDays.add(LocalDate.of(selectedMonth.year, selectedMonth.month, dayIndex))
            }

            // Add nulls for days after the last of the month to complete the grid
            while (totalDays.size % 7 != 0) {
                totalDays.add(null)
            }

            totalDays
        }

        Column(
            modifier = Modifier
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
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
            CalendarDaysGrid(days, selectedDate, viewModel = viewModel)
        }
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
    // Start with Sunday
    val daysOfWeek = listOf(
        DayOfWeek.SUNDAY,
        DayOfWeek.MONDAY,
        DayOfWeek.TUESDAY,
        DayOfWeek.WEDNESDAY,
        DayOfWeek.THURSDAY,
        DayOfWeek.FRIDAY,
        DayOfWeek.SATURDAY
    )

    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        daysOfWeek.forEach { dayOfWeek ->
            Text(text = dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.getDefault()), modifier = Modifier.weight(1f), textAlign = TextAlign.Center)
        }
    }
}


@Composable
fun CalendarDaysGrid(
    days: List<LocalDate?>,
    selectedDate: LocalDate,
    viewModel: MainViewModel
) {
    val backgroundColor = Color(0xFFE6E0D8) // slightly darker than 0xFFFFF9F4
    val roundedCornerShape = RoundedCornerShape(4.dp) // for slightly rounded corners
    val minimumTextSize = 15.sp

    LaunchedEffect(key1 = viewModel.databaseVersion) {
        Log.d(javaClass.simpleName,"database version ${viewModel.databaseVersion}")
    }

    LazyVerticalGrid(
        columns = GridCells.Fixed(7),
        contentPadding = PaddingValues(4.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        items(days) { day ->
            if (day != null) {
                val textColor = if (selectedDate == day) Color.White else Color.Black // Ensuring contrast
                Button(
                    onClick = { viewModel.setNewDate(day)},
                    modifier = Modifier
                        .padding(4.dp)
                        .aspectRatio(1f),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = when {
                            selectedDate == day -> Color.Gray
                            else -> backgroundColor
                        }
                    ),
                    contentPadding = PaddingValues(1.dp),
                    shape = roundedCornerShape // apply rounded corner shape
                ) {
                    Text(
                        text = day.dayOfMonth.toString(),
                        color = textColor, // Use the specified text color
                        fontSize = minimumTextSize // ensure text size is at least 12sp
                    )
                }
            } else {
                Spacer(modifier = Modifier
                    .padding(4.dp)
                    .aspectRatio(1f))
            }
        }
    }
}


@Composable
fun MasterManagerView(onClickBack: () -> Unit, viewModel: MainViewModel) {
    //Debug

    // If you're using State in ViewModel
    val selectedDate = viewModel.selectedDateState
    val expectedIncome = remember { mutableStateOf("") }
    val expectedExpenditure = remember { mutableStateOf("") }
    val customNotes = remember { mutableStateOf("") }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Spacer(modifier = Modifier.weight(1f)) // This pushes the button to the end of the row
                Button(
                    onClick = { onClickBack() },
                    modifier = Modifier.padding(
                        top = 8.dp,
                        end = 8.dp
                    ) // This is the padding on the right side of the button
                ) {
                    Icon(
                        imageVector = Icons.Filled.ArrowBack,
                        contentDescription = "Main Menu"
                    )
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {

                    Text("Edit Day: ${selectedDate.format(DateTimeFormatter.ofPattern("MMM dd, yyyy"))}",
                        modifier = Modifier.align(Alignment.CenterHorizontally))
                    Spacer(modifier = Modifier.height(16.dp))

                    OutlinedTextField(
                        value = expectedIncome.value,
                        onValueChange = { expectedIncome.value = it },
                        label = { Text("Expected Income") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth(fraction = 1f)
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = expectedExpenditure.value,
                        onValueChange = { expectedExpenditure.value = it },
                        label = { Text("Expected Expenditure") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth(fraction = 1f)
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = customNotes.value,
                        onValueChange = { customNotes.value = it },
                        label = { Text("Notes") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                        modifier = Modifier.fillMaxWidth(fraction = 1f)
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    Button(onClick = {
                        val income = expectedIncome.value.toDoubleOrNull() ?: 0.0
                        val expenditure = expectedExpenditure.value.toDoubleOrNull() ?: 0.0
                        val notes = customNotes.value

                        // Log the selectedDate value
                        Log.d("Debug", "Selected date for update: $selectedDate")

                        viewModel.updateFinance(selectedDate, income, expenditure, notes)
                        onClickBack()
                    },
                        modifier = Modifier
                            .fillMaxWidth(fraction = 1f)
                            .padding(40.dp)
                    ) {
                        Text("Save Changes")
                    }
                }
            }
        }
    }
}

