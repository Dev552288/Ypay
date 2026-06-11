package com.example.gms.utils

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.gms.data.model.Transaction
import com.example.gms.presentation.ui.HistoryScreen
import com.example.gms.presentation.ui.HomeScreen
import com.example.gms.presentation.ui.PaymentAmountScreen
import com.example.gms.presentation.ui.PlaceholderScreen
import com.example.gms.presentation.ui.QRScannerScreen
import com.example.gms.presentation.ui.UpiPinScreen
import com.example.gms.viewmodel.MainViewModel
import java.net.URLDecoder

@Composable
fun AppNavGraph(
    navController: NavHostController,
    navViewModel: MainViewModel
) {
    // ── Consume navigation events
    val event by navViewModel.navigationEvent.collectAsState()

    LaunchedEffect(event) {
        when (val e = event) {
            is NavigationEvent.NavigateTo -> {
                navController.navigate(e.route) {
                    e.popUpToRoute?.let { popUpTo(it) { inclusive = e.inclusive } }
                    launchSingleTop = e.launchSingleTop
                }
                navViewModel.onNavigationEventConsumed()
            }

            NavigationEvent.NavigationBack -> {
                navController.popBackStack()
                navViewModel.onNavigationEventConsumed()
            }

            null -> Unit
        }
    }

    NavHost(
        navController = navController,
        startDestination = Screen.Home.route
    ) {

        // ── Tab: Home
        composable(Screen.Home.route) {
            HomeScreen(
                viewModel = navViewModel,
                navController
            )
        }

        // ── Tab: Offers
        composable(Screen.Offers.route) {
            PlaceholderScreen(title = "Offers & Deals")
        }

        // ── Tab: Profile
        composable(Screen.Profile.route) {
            PlaceholderScreen(title = "Check Profile")
        }

        composable(Screen.History.route) {
            val transactions by navViewModel.transactions.collectAsState()

            HistoryScreen(
                viewModel = navViewModel,
                onBack = { navController.popBackStack() }
            )
        }

        // ── Payment: Scanner
        composable(Screen.Scanner.route) {
            QRScannerScreen(
                onQrCodeScanned = { raw ->
                    navViewModel.openPaymentAmount(Utils.extractUpiId(raw))
                },
                onBack = { navViewModel.navigateBack() }
            )
        }

        // ── Payment: Amount
        composable(
            route = Screen.PaymentAmount.route,
            arguments = listOf(navArgument("recipientInfo") { type = NavType.StringType })
        ) { back ->
            val recipient = URLDecoder.decode(
                back.arguments?.getString("recipientInfo").orEmpty(), "UTF-8"
            )
            PaymentAmountScreen(
                recipientInfo = recipient,
                onBack = { navViewModel.navigateBack() },
                onProceed = { amount -> navViewModel.openUpiPin(amount, recipient) }
            )
        }

        // ── Payment: UPI PIN
        composable(
            route = Screen.UpiPin.route,
            arguments = listOf(
                navArgument("amount") { type = NavType.StringType },
                navArgument("recipient") { type = NavType.StringType }
            )
        ) { back ->
            val amount = URLDecoder.decode(back.arguments?.getString("amount").orEmpty(), "UTF-8")
            val recipient =
                URLDecoder.decode(back.arguments?.getString("recipient").orEmpty(), "UTF-8")

            UpiPinScreen(
                amount = amount,
                recipient = recipient,
                onPinSuccess = {
                    navViewModel.addTransaction(
                        Transaction(
                            title = recipient,
                            amount = "₹$amount",
                            name = "Debendra Bharatia",
                            time = navViewModel.getCurrentData(),
                            date = navViewModel.getCurrentData()
                        )
                    )
                    navViewModel.onPaymentSuccess()
                },
                onBack = { navViewModel.navigateBack() }
            )
        }
    }
}

