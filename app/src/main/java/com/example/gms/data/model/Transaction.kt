package com.example.gms.data.model

data class Transaction(
    val name: String,
    val amount: String,
    val date: String,
    val isDebit: Boolean = true
)