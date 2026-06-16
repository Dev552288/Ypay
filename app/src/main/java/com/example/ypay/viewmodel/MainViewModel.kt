package com.example.ypay.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ypay.data.model.Transaction
import com.example.ypay.data.room.entity.TransactionEntity
import com.example.ypay.data.room.repo.TransactionRepository
import com.example.ypay.utils.NavigationEvent
import com.example.ypay.utils.Screen
import com.example.ypay.utils.mapper.toUi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch


class MainViewModel(private val repository: TransactionRepository) : ViewModel() {
    private val _isAuthenticated  = MutableStateFlow(false)
    val isAuthenticated: StateFlow<Boolean> = _isAuthenticated
    // New state for the error message
    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage
    private val savePin = "1234"

    private val _currentTab = MutableStateFlow<Screen>(Screen.Home)
    val currentTab: StateFlow<Screen> = _currentTab.asStateFlow()

    private val _navigationEvent = MutableStateFlow<NavigationEvent?>(null)
    val navigationEvent: StateFlow<NavigationEvent?> = _navigationEvent.asStateFlow()

    val transactions: StateFlow<List<Transaction>> =
        repository.transactions
            .map { list ->
                list.map { it.toUi() }
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = emptyList()
            )

    fun validatePin(input : String){
        if (input == savePin) {
            _isAuthenticated.value = true
            _errorMessage.value = null
        } else {
            _isAuthenticated.value = false
            _errorMessage.value = "Incorrect PIN. Please try again."
        }
    }
    fun biometricSuccess() {
        _isAuthenticated.value = true
        _errorMessage.value = null
    }


    fun resetAuth() {
        _isAuthenticated.value = false
    }

    // ── Bottom nav
    fun selectTab(screen: Screen) {
        _currentTab.value = screen
        _navigationEvent.value = NavigationEvent.NavigateTo(
            route           = screen.route,
            popUpToRoute    = Screen.Home.route,
            inclusive       = false,
            launchSingleTop = true
        )
    }
    // ── Payment flow
    fun openScanner() {
        _navigationEvent.value = NavigationEvent.NavigateTo(Screen.Scanner.route)
    }


    fun openPaymentAmount(recipientInfo: String) {
        _navigationEvent.value = NavigationEvent.NavigateTo(
            route = Screen.PaymentAmount.createRoute(recipientInfo)
        )
    }

    fun openUpiPin(amount: String, recipient: String) {
        _navigationEvent.value = NavigationEvent.NavigateTo(
            route = Screen.UpiPin.createRoute(amount, recipient)
        )
    }

    fun navigateBack() {
        _navigationEvent.value = NavigationEvent.NavigationBack
    }

    /** After PIN success — pop entire payment stack, land on Home */
    fun onPaymentSuccess() {
        _navigationEvent.value = NavigationEvent.NavigateTo(
            route        = Screen.Home.route,
            popUpToRoute = Screen.Home.route,
            inclusive    = false
        )
    }

    fun onNavigationEventConsumed() {
        _navigationEvent.value = null
    }

    fun addTransaction(transaction: Transaction) {
        viewModelScope.launch {
            repository.insert(
                TransactionEntity(
                    title = transaction.title,
                    amount = transaction.amount,
                    name = transaction.name,
                    time = transaction.time,
                    date = transaction.date
                )
            )
            Log.d("ROOM_DB", "Transaction Inserted Successfully")
        }
    }

    fun getCurrentData() : String {
        val currentDate = java.text.SimpleDateFormat(
            "dd MMM yyyy hh:mm a",
            java.util.Locale.getDefault()
        ).format(java.util.Date())
        return currentDate
    }






}