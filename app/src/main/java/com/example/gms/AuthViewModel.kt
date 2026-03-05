package com.example.gms

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class AuthViewModel : ViewModel() {
    private val _isAuthenticated  = MutableStateFlow(false)
    val isAuthenticated: StateFlow<Boolean> = _isAuthenticated
    // New state for the error message
    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage
    private val savePin = "1234"
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
        _isAuthenticated.value = false
        _errorMessage.value = null
    }


    fun resetAuth() {
        _isAuthenticated.value = false
    }
}