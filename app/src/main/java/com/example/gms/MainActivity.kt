package com.example.gms

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.fragment.app.FragmentActivity
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.rememberCameraPositionState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import com.example.gms.presentation.ui.HomeScreen
import kotlinx.coroutines.delay

class MainActivity : AppCompatActivity() {
    private lateinit var biometricHelper: BiometricHelper
    private val viewModel = AuthViewModel()
    override fun onStart() {
        super.onStart()
        viewModel.resetAuth()
    }
    @RequiresApi(Build.VERSION_CODES.P)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        biometricHelper = BiometricHelper(this)
        setContent {
            val isAuthenticated by viewModel.isAuthenticated.collectAsState()
            /*GMSTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    MapScreen()
                }
            }*/
            // This block runs whenever isAuthenticated changes
            LaunchedEffect(isAuthenticated) {
                if (!isAuthenticated) {
                    // Add a tiny delay to ensure the Window is ready
                    delay(200)
                    biometricHelper.showBiometric(
                        onSuccess = { viewModel.biometricSuccess() },
                        onError = { /* Keep them on the PIN screen */ }
                    )
                }
            }
            if (isAuthenticated){
                HomeScreen(viewModel)
            }else {
                PinScreen(viewModel)
            }
        }
    }
}

@Composable
@Preview(showBackground = true)
fun MapScreen() {
    val defaultPos = LatLng(1.35, 103.87)
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(defaultPos, 10f)
    }

    GoogleMap(
        modifier = Modifier.fillMaxSize(),
        cameraPositionState = cameraPositionState
    )
}

@Composable
fun PinScreen(viewModel: AuthViewModel) {
    var pin by remember { mutableStateOf("") }
    val isAuthenticated by viewModel.isAuthenticated.collectAsState()
    val errorMsg by viewModel.errorMessage.collectAsState() // collect error state

    if (isAuthenticated) {
        HomeScreen(viewModel)
    } else {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            errorMsg?.let {
                Text(text = it, color = Color.Red, fontSize = 16.sp)
                Spacer(modifier = Modifier.height(10.dp))
            }
            Text("Enter PIN", fontSize = 22.sp)

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "*".repeat(pin.length),
                fontSize = 30.sp
            )

            Spacer(modifier = Modifier.height(20.dp))

            NumericKeypad(
                onNumberClick = {
                    if (pin.length < 4) pin += it
                },
                onDelete = {
                    if (pin.isNotEmpty()) pin = pin.dropLast(1)
                }
            )

            Spacer(modifier = Modifier.height(20.dp))

            Button(onClick = {
                viewModel.validatePin(pin)
                pin = ""
            }) {
                Text("Verify")
            }
        }
    }
}

@Composable
fun NumericKeypad(
    onNumberClick: (String) -> Unit,
    onDelete: () -> Unit
) {
    val numbers = listOf(
        listOf("1", "2", "3"),
        listOf("4", "5", "6"),
        listOf("7", "8", "9"),
        listOf("", "0", "⌫")
    )

    Column {
        numbers.forEach { row ->
            Row(
                horizontalArrangement = Arrangement.SpaceEvenly,
                modifier = Modifier.fillMaxWidth()
            ) {
                row.forEach { item ->
                    Box(
                        modifier = Modifier
                            .size(80.dp)
                            .clickable {
                                when (item) {
                                    "⌫" -> onDelete()
                                    "" -> {}
                                    else -> onNumberClick(item)
                                }
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(item, fontSize = 24.sp)
                    }
                }
            }
        }
    }
}

