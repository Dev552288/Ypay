package com.example.gms.utils.mapper


import com.example.gms.data.model.Transaction
import com.example.gms.data.room.entity.TransactionEntity

fun TransactionEntity.toUi(): Transaction {
    return Transaction(
        title = title,
        amount = amount,
        name = name,
        time = time,
        date = date
    )
}