package com.example.gms.data.model

data class Transaction(
    val title: String? = null,
    val name: String? = null,
    val amount: String? = null,
    val date: String? = null,
    val isDebit: Boolean? = true,
    val time : String? = null
)