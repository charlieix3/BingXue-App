package com.example.assignmentexample.screen

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.assignmentexample.data.CreditCard.CreditCardEntity
import com.example.assignmentexample.data.CreditCard.CreditCardViewModel


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddCreditCardScreen(navController: NavController, paymentViewModel: CreditCardViewModel) {
    var cardNumber by remember { mutableStateOf("") }
    var cardHolder by remember { mutableStateOf("") }
    var expiryDate by remember { mutableStateOf("") }
    val context = LocalContext.current

    var isCardNumberError by remember { mutableStateOf(false) }
    var isCardHolderError by remember { mutableStateOf(false) }
    var isExpiryDateError by remember { mutableStateOf(false) }

    fun validateFields(): Boolean {
        //Cardholder name cannot be blank.
        isCardHolderError = cardHolder.isBlank()

        //Card number must not be blank and must contain only digits.
        isCardNumberError = cardNumber.isBlank() || !cardNumber.all { it.isDigit() }

        //Expiry date must match the MM/YY format
        val expiryRegex = """^(0[1-9]|1[0-2])/([0-9]{2})$""".toRegex()
        isExpiryDateError = !expiryRegex.matches(expiryDate)

        return !isCardHolderError && !isCardNumberError && !isExpiryDateError
    }

    Scaffold(topBar = { TopAppBar(
        title = { Text("Add a Credit Card", color = Color.White) },
        navigationIcon = {
            IconButton(onClick = { navController.popBackStack() }) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back", tint = Color.White)
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF2D3B47))
    ) }) { padding ->
        Column(
            modifier = Modifier.padding(padding).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OutlinedTextField(
                value = cardNumber,
                onValueChange = {
                    //only allow numeric input for card number
                    if (it.all { char -> char.isDigit() }) {
                        cardNumber = it
                    }
                    isCardNumberError = false
                },
                label = { Text("Card Number") },
                modifier = Modifier.fillMaxWidth(),
                isError = isCardNumberError,
                supportingText = { if (isCardNumberError) Text("Please enter a valid card number") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )

            OutlinedTextField(
                value = cardHolder,
                onValueChange = { cardHolder = it; isCardHolderError = false },
                label = { Text("Cardholder Name") },
                modifier = Modifier.fillMaxWidth(),
                isError = isCardHolderError,
                supportingText = { if (isCardHolderError) Text("Cardholder name cannot be empty") }
            )

            OutlinedTextField(
                value = expiryDate,
                onValueChange = { expiryDate = it; isExpiryDateError = false },
                label = { Text("Expiry Date (MM/YY)") },
                modifier = Modifier.fillMaxWidth(),
                isError = isExpiryDateError,
                supportingText = { if (isExpiryDateError) Text("Please use MM/YY format (e.g., 09/25)") }
            )
            Button(onClick = {
                if (validateFields()) {
                    val last4 = if (cardNumber.length >= 4) cardNumber.takeLast(4) else ""
                    val newCard = CreditCardEntity(
                        userOwnerId = "",
                        cardHolderName = cardHolder.trim(),
                        cardNumberLast4 = last4,
                        cardExpiryDate = expiryDate
                    )
                    paymentViewModel.addCreditCard(newCard)
                    navController.popBackStack()
                } else {
                    Toast.makeText(context, "Please fix the errors before saving.", Toast.LENGTH_SHORT).show()
                }
            },
                modifier = Modifier.fillMaxWidth().height(50.dp)
            ) {
                Text("Save Card")
            }
        }
    }
}