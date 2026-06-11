package com.example.ypay.presentation.ui

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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Contacts
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.PhoneIphone
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
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
import androidx.navigation.NavHostController
import com.example.ypay.utils.ActionItem
import com.example.ypay.viewmodel.MainViewModel
import com.example.ypay.data.model.Transaction

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: MainViewModel,
    navController: NavHostController
) {
    val currentTab by viewModel.currentTab.collectAsState()
    val transactions by viewModel.transactions.collectAsState()
    Scaffold(
        bottomBar = {
            YPayBottomNavigation(
                navController
            )
        }
    ) { innerPadding ->
        MainDashboard(
            onScanClick = { viewModel.openScanner() },
            viewModel = viewModel,
            transactions = transactions
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainDashboard(
    onScanClick: () -> Unit,
    viewModel: MainViewModel,
    transactions: List<Transaction>
) {
    val context = LocalContext.current
    var isSearchActive by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    if (isSearchActive) {
                        TextField(
                            value = searchQuery,
                            onValueChange = { searchQuery = it },
                            placeholder = { Text("Search...", fontSize = 16.sp) },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            colors = TextFieldDefaults.colors(
                                focusedContainerColor = Color.Transparent,
                                unfocusedContainerColor = Color.Transparent,
                                focusedIndicatorColor = Color.Transparent,
                                unfocusedIndicatorColor = Color.Transparent
                            ),
                            trailingIcon = {
                                if (searchQuery.isNotEmpty()) {
                                    IconButton(onClick = { searchQuery = "" }) {
                                        Icon(Icons.Default.Close, contentDescription = "Clear")
                                    }
                                }
                            }
                        )
                    } else {
                        Text("Y-Pay", fontWeight = FontWeight.Bold)
                    }
                },
                navigationIcon = {
                    if (isSearchActive) {
                        IconButton(onClick = {
                            isSearchActive = false
                            searchQuery = "" // Clear search on exit
                        }) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                        }
                    }
                },
                actions = {
                    if (!isSearchActive) {
                        IconButton(onClick = { isSearchActive = true }) {
                            Icon(Icons.Default.Search, contentDescription = "Search")
                        }
                    }
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
            // 1. Large Scan QR Button (The Y-Pay Signature)
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
                            Icon(action.icon,
                                contentDescription = null,
                                tint = Color.Black
                            )
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
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 32.dp)
            ) {
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
                        TransactionRow(name = tx.name?:"", amount = tx.amount?:"", date = tx.date?:"")
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