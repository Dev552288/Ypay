package com.example.gms.presentation.ui

import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
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
import androidx.compose.ui.unit.sp
import com.example.gms.NumericKeypad

@Composable
fun PaymentAmountScreen(
    recipientInfo: String,
    onBack: () -> Unit,
    onProceed: (String) -> Unit
) {
    var amount by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            IconButton(onClick = onBack, modifier = Modifier.padding(16.dp)) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Recipient Profile Section
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .background(Color(0xFF1A73E8), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = recipientInfo.take(1).uppercase(),
                    color = Color.White,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Text(
                text = "Paying $recipientInfo",
                modifier = Modifier.padding(top = 12.dp),
                style = MaterialTheme.typography.titleMedium
            )

            Spacer(modifier = Modifier.height(40.dp))

            // Amount Input
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("₹", fontSize = 48.sp, fontWeight = FontWeight.Normal)
                Text(
                    text = amount.ifEmpty { "0" },
                    fontSize = 56.sp,
                    color = if (amount.isEmpty()) Color.LightGray else Color.Black,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            // Custom Number Pad for Amount
            NumericKeypad(
                onNumberClick = { if (amount.length < 7) amount += it },
                onDelete = { if (amount.isNotEmpty()) amount = amount.dropLast(1) }
            )

            // Pay Button
            Button(
                onClick = {onProceed(amount)},
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .height(56.dp),
                shape = RoundedCornerShape(28.dp),
                enabled = amount.isNotEmpty() && amount != "0",
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1A73E8))
            ) {
                Text("Proceed to pay", fontSize = 16.sp)
            }
        }
    }
}