package com.example.assignmentexample.screen

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.assignmentexample.data.Address.AddressEntity
import com.example.assignmentexample.data.Address.AddressViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddAddressScreen(navController: NavController, addressViewModel: AddressViewModel) {
    var addressLine1 by remember { mutableStateOf("") }
    var city by remember { mutableStateOf("") }
    var postcode by remember { mutableStateOf("") }
    var state by remember { mutableStateOf("") }
    val context = LocalContext.current

    var isAddressLine1Error by remember { mutableStateOf(false) }
    var isCityError by remember { mutableStateOf(false) }
    var isPostcodeError by remember { mutableStateOf(false) }
    var isStateError by remember { mutableStateOf(false) }

    fun validateFields(): Boolean {
        isAddressLine1Error = addressLine1.isBlank()
        isCityError = city.isBlank()
        isStateError = state.isBlank()
        //postcode must not be blank and must contain at least one digit
        isPostcodeError = postcode.isBlank() || !postcode.any { it.isDigit() }

        return !isAddressLine1Error && !isCityError && !isStateError && !isPostcodeError
    }

    Scaffold(
        topBar = { TopAppBar(
            title = { Text("Add New Address", color = Color.White) },
            navigationIcon = {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back", tint = Color.White)
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF2D3B47))
        ) })


    { padding ->
        Column(Modifier.padding(padding).padding(16.dp)) {
            OutlinedTextField(
                value = addressLine1,
                onValueChange = { addressLine1 = it; isAddressLine1Error = false },
                label = { Text("Address Line 1") },
                modifier = Modifier.fillMaxWidth(),
                isError = isAddressLine1Error,
                supportingText = { if (isAddressLine1Error) Text("Address cannot be empty") }
            )
            OutlinedTextField(
                value = city,
                onValueChange = { city = it; isCityError = false },
                label = { Text("City") },
                modifier = Modifier.fillMaxWidth(),
                isError = isCityError,
                supportingText = { if (isCityError) Text("City cannot be empty") }
            )
            OutlinedTextField(
                value = postcode,
                onValueChange = { postcode = it; isPostcodeError = false },
                label = { Text("Postcode") },
                modifier = Modifier.fillMaxWidth(),
                isError = isPostcodeError,
                supportingText = { if (isPostcodeError) Text("Please enter a valid postcode") }
            )
            OutlinedTextField(
                value = state,
                onValueChange = { state = it; isStateError = false },
                label = { Text("State") },
                modifier = Modifier.fillMaxWidth(),
                isError = isStateError,
                supportingText = { if (isStateError) Text("State cannot be empty") }
            )
            Spacer(Modifier.height(16.dp))
            Button(
                onClick = {
                    if (validateFields()) {
                        val newAddress = AddressEntity(
                            userOwnerId = "",
                            addressLine1 = addressLine1.trim(),
                            city = city.trim(),
                            postcode = postcode.trim(),
                            state = state.trim(),
                            isDefault = false
                        )
                        addressViewModel.addAddress(newAddress)
                        navController.popBackStack()
                    } else {
                        Toast.makeText(context, "Please fill in all fields correctly.", Toast.LENGTH_SHORT).show()
                    }
                },
                modifier = Modifier.fillMaxWidth().height(50.dp)
            ) {
                Text("Save Address")
            }
        }
    }
}