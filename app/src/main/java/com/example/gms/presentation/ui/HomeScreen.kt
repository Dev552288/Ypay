package com.example.gms.presentation.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.Contacts
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.PhoneIphone
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.gms.ActionItem
import com.example.gms.AuthViewModel
import com.example.gms.Transaction
import com.example.gms.utils.Utils

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(viewModel: AuthViewModel) {
    var showScanner by remember { mutableStateOf(false) }
    var paymentData by remember { mutableStateOf<String?>(null) } // Stores QR result
    var amountToPay by remember { mutableStateOf("") }
    var showPinScreen by remember { mutableStateOf(false) }
    // Initial dummy data
    var transactions by remember {
        mutableStateOf(
            listOf(
                Transaction("Coffee Shop", "₹5.50", "Today, 10:30 AM"),
                Transaction("Electricity Bill", "₹120.00", "Yesterday")
            )
        )
    }

    when {
        // STEP 4: UPI PIN Screen
        showPinScreen -> {
            UpiPinScreen(
                amount = amountToPay,
                recipient = paymentData ?: "",
                onPinSuccess = {
                    val newTx = Transaction(paymentData!!, "₹$amountToPay", "Just now")
                    transactions = listOf(newTx) + transactions
                    // Reset everything to go home
                    paymentData = null
                    amountToPay = ""
                    showPinScreen = false
                },
                onBack = { showPinScreen = false }
            )
        }

        paymentData != null -> {
            // Screen 3: Enter Amount
            PaymentAmountScreen(
                recipientInfo = paymentData!!,
                onBack = { paymentData = null },
                onProceed = { amount ->
                    amountToPay = amount
                    showPinScreen = true
                }
            )
        }
        // STEP 2: Scanner
        showScanner -> {
            // Screen 2: Scanner
            QRScannerScreen(
                onQrCodeScanned = { rawResult ->
                    paymentData = Utils.extractUpiId(rawResult) // Store result and switch screen
                    showScanner = false
                },
                onBack = { showScanner = false }
            )
        }
        // STEP 1: Main Dashboard
        else -> {
            MainDashboard(
                onScanClick = { showScanner = true }, // This "switches" the channel to Scanner
                viewModel = viewModel,
                transactions = transactions // Pass the state list here
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainDashboard(
    onScanClick: () -> Unit,
    viewModel: AuthViewModel,
    transactions: List<Transaction>
) {
    val context = LocalContext.current
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Y-Pay", fontWeight = FontWeight.Bold) },
                actions = {
                    IconButton(onClick = { viewModel.resetAuth() }) {
                        Icon(Icons.Default.Logout, contentDescription = "Lock App")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // 1. Large Scan QR Button (The G-Pay Signature)
            Button(
                onClick = onScanClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(28.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1A73E8))
            ) {
                Icon(Icons.Default.QrCodeScanner, contentDescription = null)
                Spacer(Modifier.width(8.dp))
                Text("Scan any QR code", fontSize = 16.sp)
            }

            Spacer(modifier = Modifier.height(24.dp))

            // 2. Action Grid
            val actions = listOf(
                ActionItem("Scan QR", Icons.Default.QrCode, Color(0xFFE8F0FE)),
                ActionItem("Pay contacts", Icons.Default.Contacts, Color(0xFFFEF7E0)),
                ActionItem("Pay phone", Icons.Default.PhoneIphone, Color(0xFFE6F4EA)),
                ActionItem("Bank transfer", Icons.Default.AccountBalance, Color(0xFFFCE8E6))
            )

            LazyVerticalGrid(
                columns = GridCells.Fixed(4),
                modifier = Modifier.height(110.dp), // Increased slightly for text clearance
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(actions) { action ->
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.clickable {
                            if (action.title == "Scan QR") {
                                onScanClick()
                            } else {
                                android.widget.Toast.makeText(
                                    context,
                                    "${action.title} is not available yet",
                                    android.widget.Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    ) {
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .background(action.color, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(action.icon, contentDescription = null, tint = Color.Black)
                        }
                        Text(
                            text = action.title,
                            fontSize = 11.sp,
                            modifier = Modifier.padding(top = 4.dp),
                            maxLines = 1 // Keeps the grid neat
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // 3. Recent Activity Section
            // Recent Activity Section
            Column(modifier = Modifier
                .fillMaxWidth()
                .padding(top = 32.dp)) {
                Text("Recent activity", fontWeight = FontWeight.Bold)

                Spacer(modifier = Modifier.height(16.dp))

                // 3. Render the dynamic list
                if (transactions.isEmpty()) {
                    Text(
                        "No recent transactions",
                        color = Color.Gray,
                        modifier = Modifier.padding(vertical = 20.dp)
                    )
                } else {
                    transactions.forEach { tx ->
                        TransactionRow(tx.name, tx.amount, tx.date)
                    }
                }
                Spacer(modifier = Modifier.height(20.dp))
            }
        }
    }
}

@Composable
fun TransactionRow(name: String, amount: String, date: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .background(Color.LightGray, CircleShape)
        )
        Spacer(Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(name, fontWeight = FontWeight.Medium)
            Text(date, fontSize = 12.sp, color = Color.Gray)
        }
        Text(amount, fontWeight = FontWeight.Bold)
    }
}