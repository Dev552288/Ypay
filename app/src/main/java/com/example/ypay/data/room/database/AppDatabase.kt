package com.example.ypay.data.room.database

import android.content.Context
import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.ypay.data.room.dao.TransactionDao
import com.example.ypay.data.room.entity.TransactionEntity
@Database(entities = [TransactionEntity::class], version = 1, exportSchema = false)
abstract class AppDatabase: RoomDatabase() {
    abstract fun transactionDao() : TransactionDao

    companion object {
        @Volatile
        private var  INSTANSE : AppDatabase? = null
        fun  getDataBase(context: Context): AppDatabase{
            return INSTANSE?:synchronized(this){
                val instance = androidx.room.Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "app_database"
                ).build()
                INSTANSE = instance
                instance
            }
        }
    }
}