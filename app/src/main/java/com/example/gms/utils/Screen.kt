package com.example.gms.utils

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocalOffer
import androidx.compose.material.icons.filled.Person

sealed class Screen(
    val route: String,
    val label: String,
    val icon: androidx.compose.ui.graphics.vector.ImageVector
) {
    // ── Bottom nav tabs (your original 3 — UNCHANGED)
    object Home : Screen("home", "Home", Icons.Default.Home)
    object Offers : Screen("offers", "Offers", Icons.Default.LocalOffer)
    object Profile : Screen("profile", "Profile", Icons.Default.Person)

    // ── Payment flow (new — not shown in bottom nav)
    object Scanner : Screen("scanner", "", Icons.Default.Home)

    object PaymentAmount : Screen("payment_amount/{recipientInfo}", "", Icons.Default.Home) {
        fun createRoute(recipientInfo: String) =
            "payment_amount/${java.net.URLEncoder.encode(recipientInfo, "UTF-8")}"
    }

    object UpiPin : Screen("upi_pin/{amount}/{recipient}", "", Icons.Default.Home) {
        fun createRoute(amount: String, recipient: String) =
            "upi_pin/${java.net.URLEncoder.encode(amount, "UTF-8")}/${
                java.net.URLEncoder.encode(
                    recipient,
                    "UTF-8"
                )
            }"
    }

    companion object {
        /** Drives GPayBottomNavigation — your existing 3 tabs */
        val bottomNavItems: List<Screen> = listOf(Home, Offers, Profile)
    }
}