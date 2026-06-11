package com.example.ypay.presentation.base

import android.app.Application
import androidx.room.Room
import com.example.ypay.data.room.database.AppDatabase

class MyApp : Application() {
    override fun onCreate() {
        super.onCreate()
        val database: AppDatabase by lazy {
            Room.databaseBuilder(
                applicationContext,
                AppDatabase::class.java,
                "transaction_db"
            ).build()
        }
    }
}