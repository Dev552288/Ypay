package com.example.gms.viewmodel

import androidx.lifecycle.ViewModel
import com.example.gms.data.model.Transaction
import com.example.gms.utils.NavigationEvent
import com.example.gms.utils.Screen
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class MainViewModel : ViewModel() {
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

    // ── Transactions state (FIX — moved here from HomeScreen)
    private val _transactions = MutableStateFlow<List<Transaction>>(emptyList())
    val transactions: StateFlow<List<Transaction>> = _transactions.asStateFlow()

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
        _transactions.value = listOf(transaction) + _transactions.value
    }

    fun getCurrentData() : String {
        val currentDate = java.text.SimpleDateFormat(
            "dd MMM yyyy",
            java.util.Locale.getDefault()
        ).format(java.util.Date())
        return currentDate
    }
}