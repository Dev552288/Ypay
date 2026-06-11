package com.example.gms.presentation.base

import android.os.Build
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import androidx.room.Room
import com.example.gms.data.room.dao.TransactionDao
import com.example.gms.data.room.database.AppDatabase
import com.example.gms.data.room.repo.TransactionRepository
import com.example.gms.presentation.ui.PinScreen
import com.example.gms.utils.AppNavGraph
import com.example.gms.utils.BiometricHelper
import com.example.gms.viewmodel.MainViewModel
import com.example.gms.viewmodel.MainViewModelFactory
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

