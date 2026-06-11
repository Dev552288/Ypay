package com.example.ypay.presentation.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ypay.viewmodel.MainViewModel

@Composable
fun PinScreen(viewModel: MainViewModel) {
    var pin by remember { mutableStateOf("") }
    val errorMsg by viewModel.errorMessage.collectAsState() // collect error state

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        errorMsg?.let {
            Text(text = it, color = Color.Red, fontSize = 16.sp)
            Spacer(modifier = Modifier.height(10.dp))
        }
        Text("Enter PIN", fontSize = 22.sp)

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = "*".repeat(pin.length),
            fontSize = 30.sp
        )

        Spacer(modifier = Modifier.height(20.dp))

        NumericKeypad(
            onNumberClick = {
                if (pin.length < 4) pin += it
            },
            onDelete = {
                if (pin.isNotEmpty()) pin = pin.dropLast(1)
            }
        )

        Spacer(modifier = Modifier.height(20.dp))

        Button(onClick = {
            viewModel.validatePin(pin)
            pin = ""
        }) {
            Text("Verify")
        }
    }
}