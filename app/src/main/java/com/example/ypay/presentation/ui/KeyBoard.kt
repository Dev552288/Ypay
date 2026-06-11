package com.example.ypay.presentation.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun NumericKeypad(
    onNumberClick: (String) -> Unit,
    onDelete: () -> Unit
) {
    val numbers = listOf(
        listOf("1", "2", "3"),
        listOf("4", "5", "6"),
        listOf("7", "8", "9"),
        listOf("", "0", "⌫")
    )
    Column {
        numbers.forEach { row ->
            Row(
                horizontalArrangement = Arrangement.SpaceEvenly,
                modifier = Modifier.fillMaxWidth()
            ) {
                row.forEach { item ->
                    Box(
                        modifier = Modifier
                            .size(80.dp)
                            .clickable {
                                when (item) {
                                    "⌫" -> onDelete()
                                    "" -> {}
                                    else -> onNumberClick(item)
                                }
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(item, fontSize = 24.sp)
                    }
                }
            }
        }
    }
}