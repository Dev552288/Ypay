package com.example.gms.presentation.base

import android.app.Application
import androidx.room.Room
import com.example.gms.data.room.database.AppDatabase

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