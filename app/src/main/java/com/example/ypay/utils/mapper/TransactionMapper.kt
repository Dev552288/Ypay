package com.example.ypay.utils.mapper


import com.example.ypay.data.model.Transaction
import com.example.ypay.data.room.entity.TransactionEntity

fun TransactionEntity.toUi(): Transaction {
    return Transaction(
        qrCode = qrcode,
        amount = amount,
        name = name,
        time = time,
        date = date
    )
}