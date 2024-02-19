package com.example.myapplication.android

import android.os.Bundle
import android.widget.Space
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Canvas
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
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.BlendMode.Companion.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.myapplication.Greeting
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.sp
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Calendar
import java.util.Locale

data class ProgressBarSection(
    val value: Float,
    val color: Color
)

//global date
val selectedDate = mutableStateOf(LocalDate.now())

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyApplicationTheme(darkTheme = false) {
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
    var isTopIndex by remember { mutableStateOf(true) }
    if (isTopIndex) {
        IndexViewAbove(onSwitch = { isTopIndex = false })
    } else {
        IndexViewBelow(onSwitch = { isTopIndex = true })
    }
}


@Composable
fun IndexViewAbove(onSwitch: () -> Unit) {
    var currentPageState by remember { mutableStateOf("Main") }
    when (currentPageState) {
        "Calendar" -> CalendarView { currentPageState = "Main" }
        "MasterManager" -> MasterManagerView { currentPageState = "Main" }
        "Main" -> MainView(
            onCalendarClick = { currentPageState = "Calendar" },
            onMasterManagerClick = { currentPageState = "MasterManager" },
            onPreviewClick = onSwitch
        )
    }
}

@Composable
fun IndexViewBelow(onSwitch: () -> Unit) {
    var currentPageState by remember { mutableStateOf("Preview") }
    when (currentPageState) {
        "Calendar" -> CalendarView { currentPageState = "Preview" }
        "MasterManager" -> MasterManagerView { currentPageState = "Preview" }
        "Preview" -> PreviewView(
            onCalendarClick = { currentPageState = "Calendar" },
            onMasterManagerClick = { currentPageState = "MasterManager" },
            onMainClick = onSwitch
        )
    }
}
@Composable
fun MainView(onCalendarClick: () -> Unit, onMasterManagerClick: () -> Unit, onPreviewClick: () -> Unit) {
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
            // This Box will take up all available space, pushing the progress bar to the center
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f), // This will make the box take up all available space
                contentAlignment = Alignment.Center // This will align the progress bar in the center of the box
            ) {
                PieChart(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(30.dp)
                        .padding(horizontal = 32.dp),  // Side paddings for the progress bar
                    sections = listOf(
                        ProgressBarSection(
                            value = 0.2f,
                            color = androidx.compose.ui.graphics.Color.Red
                        ),
                        ProgressBarSection(
                            value = 0.5f,
                            color = androidx.compose.ui.graphics.Color.Blue
                        ),
                        ProgressBarSection(
                            value = 0.3f,
                            color = androidx.compose.ui.graphics.Color.Green
                        )
                    )
                )
            }
            IconButton(
                onClick = { onPreviewClick() },
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(bottom = 16.dp)
            ) {
                Icon(
                    imageVector = Icons.Filled.KeyboardArrowDown,
                    contentDescription = "Go to Preview",
                    modifier = Modifier.size(60.dp)
                )
            }
        }
    }
}

@Composable
fun PreviewView(onCalendarClick: () -> Unit, onMasterManagerClick: () -> Unit, onMainClick: () -> Unit) {
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

                // IconButton at the top of the PreviewView
                IconButton(
                    onClick = { onMainClick() },
                    modifier = Modifier.size(60.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.KeyboardArrowUp,
                        contentDescription = "Go to Main"
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
            Text(
                text = selectedDate.value.format(DateTimeFormatter.ofPattern("yyyy MM dd")),
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
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
        val totalPercentage = sections.sumByDouble { it.value.toDouble() }.toFloat()
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
                androidx.compose.ui.graphics.Color.Red -> Color(0xFFBB6666) // Less vibrant red
                androidx.compose.ui.graphics.Color.Blue -> Color(0xFF6666BB)
                androidx.compose.ui.graphics.Color.Green -> Color(0xFF66BB66) // Less vibrant green
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
fun CalendarView(onClickBack: () -> Unit) {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        var selectedMonth by remember { mutableStateOf(YearMonth.from(selectedDate.value)) }
        val daysInMonth = remember(selectedMonth) { selectedMonth.lengthOfMonth() }
        val firstOfMonth = remember(selectedMonth) { selectedMonth.atDay(1) }
        val lastOfMonth = remember(selectedMonth) { selectedMonth.atEndOfMonth() }
        val startDayOfWeek = firstOfMonth.dayOfWeek.value
        val days = remember(selectedMonth) {
            val totalDays = List(daysInMonth + startDayOfWeek - 1) { dayIndex ->
                if (dayIndex >= startDayOfWeek) {
                    LocalDate.of(
                        selectedMonth.year,
                        selectedMonth.month,
                        dayIndex - startDayOfWeek + 1
                    )
                } else null
            }
            // Fill in the leading and trailing nulls to complete the week
            val additionalDaysAtStart = startDayOfWeek - 1
            val additionalDaysAtEnd = 7 - (totalDays.size % 7)
            List(additionalDaysAtStart) { null } + totalDays + List(additionalDaysAtEnd) { null }
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
            CalendarDaysGrid(days, selectedDate)
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
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        DayOfWeek.values().forEach { dayOfWeek ->
            Text(text = dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.getDefault()), modifier = Modifier.weight(1f), textAlign = TextAlign.Center)
        }
    }
}

@Composable
fun CalendarDaysGrid(
    days: List<LocalDate?>,
    selectedDate: MutableState<LocalDate>
) {
    val backgroundColor = Color(0xFFE6E0D8) // slightly darker than 0xFFFFF9F4
    val roundedCornerShape = RoundedCornerShape(4.dp) // for slightly rounded corners
    val minimumTextSize = 15.sp

    LazyVerticalGrid(
        columns = GridCells.Fixed(7),
        contentPadding = PaddingValues(4.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        items(days) { day ->
            if (day != null) {
                val textColor = if (selectedDate.value == day) androidx.compose.ui.graphics.Color.White else androidx.compose.ui.graphics.Color.Black // Ensuring contrast
                Button(
                    onClick = { selectedDate.value = day },
                    modifier = Modifier
                        .padding(4.dp)
                        .aspectRatio(1f),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (selectedDate.value == day) androidx.compose.ui.graphics.Color.Gray else backgroundColor
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
fun MasterManagerView(onClickBack: () -> Unit) {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
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
                    imageVector = Icons.Filled.Home,
                    contentDescription = "Main Menu"
                )
            }
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
