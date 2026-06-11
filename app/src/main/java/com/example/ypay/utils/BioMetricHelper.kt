package com.example.ypay.utils

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity

class BiometricHelper(private val activity: FragmentActivity) {
    @RequiresApi(Build.VERSION_CODES.P)
    fun showBiometric(
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        val executor = ContextCompat.getMainExecutor(activity)

        val biometricPrompt = BiometricPrompt(
            activity,
            executor,
            @RequiresApi(Build.VERSION_CODES.P)
            object : BiometricPrompt.AuthenticationCallback() {

                override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                    onSuccess()
                }

                override fun onAuthenticationError(code: Int, errString: CharSequence) {
                    onError(errString.toString())
                }

                override fun onAuthenticationFailed() {
                    onError("Authentication failed")
                }
            })

        val promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle("Login")
            .setSubtitle("Authenticate using fingerprint")
            .setNegativeButtonText("Use PIN")
            .build()

        biometricPrompt.authenticate(promptInfo)
    }
}