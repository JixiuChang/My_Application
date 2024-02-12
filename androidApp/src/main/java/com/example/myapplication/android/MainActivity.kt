package com.example.myapplication.android

import android.os.Bundle
import android.widget.Space
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.myapplication.Greeting

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
                    //GreetingView(Greeting().greet())
                }
            }
        }
    }
}

@Composable
fun IndexView() {
    var currentPageState by remember{ mutableStateOf("Greeting") }
    when (currentPageState) {
        "Calendar" -> CalendarView() {
            currentPageState = "Greeting"
        }
        else -> GreetingView("greeting") {
            currentPageState = "Calendar"
        }
    }
}

@Composable
fun CalendarView(onClickBack: () -> Unit) {
    //Text("Calendar")
    Button(onClick = { onClickBack() }) {
        Text("Back")
    }
}

@Composable
fun GreetingView(text: String, onClick: () -> Unit) {

        var state by remember{ mutableStateOf(0) }
    Row(
        modifier = Modifier.fillMaxSize()
    ) {
        Button(onClick = { onClick() }) {
            Icon(imageVector = Icons.Filled.DateRange, contentDescription = "")

        }
        Spacer(modifier = Modifier.weight(1f))
        Icon(imageVector = Icons.Filled.DateRange, contentDescription = "")
        Text(text = state.toString())
        Button(onClick = {
            state += 1
        }) {
            Text(text = "Add it")
        }
    }
    //Text(text = text)
}

@Preview
@Composable
fun DefaultPreview() {
    MyApplicationTheme {
        //GreetingView("Hello, Android!")
        IndexView()
    }
}
