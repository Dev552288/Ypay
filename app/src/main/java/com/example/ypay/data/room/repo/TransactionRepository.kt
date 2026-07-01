package com.example.ypay.data.room.repo

import com.example.ypay.data.room.dao.TransactionDao
import com.example.ypay.data.room.entity.TransactionEntity

class TransactionRepository(private val dao: TransactionDao) {
    val transactions = dao.getAllTransactions()

    suspend fun insertTxnData(transaction: TransactionEntity){
        dao.insertTxnData(transaction)
    }
}