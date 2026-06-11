package com.example.gms.data.room.repo

import com.example.gms.data.room.dao.TransactionDao
import com.example.gms.data.room.entity.TransactionEntity

class TransactionRepository(private val dao: TransactionDao) {
    val transactions = dao.getAllTransactions()

    suspend fun insert(transaction: TransactionEntity){
        dao.insertTransaction(transaction)
    }
}