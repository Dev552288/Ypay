package com.example.gms.presentation.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun UpiPinScreen(
    amount: String,
    recipient: String,
    onPinSuccess: () -> Unit,
    onBack: () -> Unit
) {
    var pin by remember { mutableStateOf("") }

    Column(
        modifier = Modifier.fillMaxSize().background(Color.White),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Header
        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("Y-Pay", fontWeight = FontWeight.Bold, color = Color.Gray)
            Text("State Bank of India", fontWeight = FontWeight.Medium)
        }

        Spacer(modifier = Modifier.height(40.dp))

        Text("ENTER UPI PIN", style = MaterialTheme.typography.labelLarge)
        Text(recipient, fontWeight = FontWeight.Bold)
        Text("₹$amount", style = MaterialTheme.typography.headlineLarge)

        Spacer(modifier = Modifier.height(24.dp))

        // PIN Dots
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            repeat(4) { index ->
                val isFilled = pin.length > index
                Box(
                    modifier = Modifier
                        .size(16.dp)
                        .background(if (isFilled) Color.Black else Color.LightGray, CircleShape)
                )
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        NumericKeypad(
            onNumberClick = { if (pin.length < 4) pin += it },
            onDelete = { if (pin.isNotEmpty()) pin = pin.dropLast(1) }
        )

        // Submit Button (Checkmark)
        IconButton(
            onClick = { if (pin.length == 4) onPinSuccess() },
            modifier = Modifier.padding(16.dp).size(64.dp).background(Color(0xFF1A73E8), CircleShape)
        ) {
            Icon(Icons.Default.Check, contentDescription = "Submit", tint = Color.White)
        }
    }
}