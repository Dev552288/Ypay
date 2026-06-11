package com.example.ypay.presentation.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.ypay.utils.Screen

@Composable
fun YPayBottomNavigation(
    navController: NavHostController
) {
    val items = listOf(Screen.Home, Screen.Offers, Screen.Profile, Screen.History)

    val currentRoute = navController.currentBackStackEntry?.destination?.route

    androidx.compose.material3.NavigationBar(
        containerColor = MaterialTheme.colorScheme.surface,
        tonalElevation = 8.dp
    ) {
        items.forEach { screen ->
            val selected = currentRoute == screen.route

            NavigationBarItem(
                icon = { Icon(screen.icon, contentDescription = screen.label) },
                label = { Text(screen.label) },
                selected = selected,
                onClick = {
                    navController.navigate(screen.route) {
                        popUpTo(Screen.Home.route) {
                            saveState = true
                        }
                        launchSingleTop = true   // ✅ prevents duplicate screens
                        restoreState = true
                    }
                },
                colors = androidx.compose.material3.NavigationBarItemDefaults.colors(
                    selectedIconColor = Color(0xFF1A73E8),
                    selectedTextColor = Color(0xFF1A73E8),
                    indicatorColor = Color(0xFFE8F0FE)
                )
            )
        }
    }
}

@Composable
fun PlaceholderScreen(title: String) {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(title, style = MaterialTheme.typography.headlineMedium)
    }
}
