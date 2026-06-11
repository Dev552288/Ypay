package com.example.ypay.data.room.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "transactions")
data class TransactionEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val title: String?= null,
    val amount: String? = null,
    val name: String? = null,
    val time: String? = null,
    val date: String? = null
)