package com.example.ypay.presentation.base

import android.os.Build
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import com.example.ypay.data.room.database.AppDatabase
import com.example.ypay.data.room.repo.TransactionRepository
import com.example.ypay.presentation.ui.PinScreen
import com.example.ypay.utils.AppNavGraph
import com.example.ypay.utils.BiometricHelper
import com.example.ypay.viewmodel.MainViewModel
import com.example.ypay.viewmodel.MainViewModelFactory
import kotlinx.coroutines.delay

class MainActivity : AppCompatActivity() {
    private lateinit var biometricHelper: BiometricHelper

    private var viewModel : MainViewModel? = null

    override fun onStart() {
        super.onStart()
    }

    @RequiresApi(Build.VERSION_CODES.P)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        biometricHelper = BiometricHelper(this)
        // ROOM DATABASE
        val database = AppDatabase.getDataBase(this)
        // Dao
        val transactionDao = database.transactionDao()
        // REPOSITORY
        val repository = TransactionRepository(transactionDao)
        // FACTORY
        val factory = MainViewModelFactory(repository)

        setContent {
            val viewModel: MainViewModel = viewModel(factory = factory)
            val isAuthenticated by viewModel.isAuthenticated.collectAsState()
            // This block runs whenever isAuthenticated changes
            LaunchedEffect(isAuthenticated) {
                if (!isAuthenticated) {
                    // Add a tiny delay to ensure the Window is ready
                    delay(200)
                    biometricHelper.showBiometric(
                        onSuccess = { viewModel.biometricSuccess() },
                        onError = { /* Keep them on the PIN screen */ }
                    )
                }
            }

            if (isAuthenticated) {
                AppNavGraph(
                    navController = rememberNavController(),
                    navViewModel = viewModel
                )
            } else {
                PinScreen(viewModel)
            }
        }
    }
}

