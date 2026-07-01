package com.example.ypay.utils

object Utils {
    fun extractUpiId(qrContent: String): String {
        // If it's a standard UPI link: upi://pay?pa=name@bank&pn=Display%20Name...
        return if (qrContent.startsWith("upi://", ignoreCase = true)) {
            val uri = android.net.Uri.parse(qrContent)
            uri.getQueryParameter("pa") ?: qrContent // Get 'pa' parameter
        } else {
            qrContent // If it's just plain text, return as is
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