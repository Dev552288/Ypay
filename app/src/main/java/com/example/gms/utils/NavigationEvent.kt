package com.example.gms.utils

sealed class NavigationEvent {
    data class NavigateTo(
        val route: String,
        val popUpToRoute: String? = null,
        val inclusive: Boolean = false,
        val launchSingleTop:Boolean = true,
    ): NavigationEvent()

    object NavigationBack : NavigationEvent()
}