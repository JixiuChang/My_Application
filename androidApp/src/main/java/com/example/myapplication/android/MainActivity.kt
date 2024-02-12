package com.example.myapplication.android

import android.os.Bundle
import android.widget.Space
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.BlendMode.Companion.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.myapplication.Greeting
import androidx.compose.ui.graphics.Color

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
    Row(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
        Button(onClick = onCalendarClick, modifier = Modifier.padding(8.dp)) {
            Icon(imageVector = Icons.Filled.DateRange, contentDescription = "Calendar")
        }
        Spacer(modifier = Modifier.weight(1f))
        Button(onClick = onMasterManagerClick, modifier = Modifier.padding(8.dp)) {
            Icon(imageVector = Icons.Filled.Edit, contentDescription = "Edit")
        }
    }
}
@Composable
fun CalendarView(onClickBack: () -> Unit) {
    Row(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
        Button(onClick = { onClickBack() }, modifier = Modifier.padding(8.dp)) {
            Icon(imageVector = Icons.Filled.Home, contentDescription = "Main Menu")
        }
    }
}

@Composable
fun MasterManagerView(onClickBack: () -> Unit) {
    Row(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
        Button(onClick = { onClickBack() }, modifier = Modifier.padding(8.dp)) {
            Icon(imageVector = Icons.Filled.Home, contentDescription = "Main Menu")
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
