package com.example.assignmentexample.screen

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.assignmentexample.R
import com.example.assignmentexample.data.Address.AddressViewModel
import com.example.assignmentexample.data.CreditCard.CreditCardEntity
import com.example.assignmentexample.data.CreditCard.CreditCardViewModel
import com.example.assignmentexample.data.SettingsManager
import com.example.assignmentexample.data.UserViewModel

@Composable
fun OrderConfirmationScreen(
    navController: NavController,
    totalAmount: String?,
    userViewModel: UserViewModel,
    paymentViewModel: CreditCardViewModel,
    addressViewModel: AddressViewModel
) {
    //create the settings manager instance here
    val context = LocalContext.current
    val settingsManager = remember { SettingsManager(context) }
    val userProfile by userViewModel.userProfile.collectAsState()
    val savedCards by paymentViewModel.savedCreditCards.collectAsState()
    val addresses by addressViewModel.allAddresses.collectAsState()
    val defaultAddress = addresses.find { it.isDefault }


    var selectedPaymentType by remember { mutableStateOf(settingsManager.defaultPaymentType) }
    var selectedCard by remember { mutableStateOf<CreditCardEntity?>(null) }
    var currentSelectedBank by remember { mutableStateOf(settingsManager.savedBank) }

    //when the screen loads or the list of cards changes, pick the first one as default
    LaunchedEffect(savedCards) {
        val defaultCard = savedCards.find { it.id == settingsManager.defaultCardId }
        selectedCard = defaultCard ?: savedCards.firstOrNull() // Fallback to the first card
    }

    val subtotal = totalAmount?.toDoubleOrNull() ?: 0.0
    val tax = subtotal * 0.10
    val deliveryFee = 4.00
    val finalTotal = subtotal + tax + deliveryFee

    Scaffold(
        topBar = {
            Row(
                modifier = Modifier.fillMaxWidth().background(Color(0xFF29475F)).statusBarsPadding().padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = Color.White,
                    modifier = Modifier.clickable { navController.navigateUp() }
                )
                Text(
                    text = "Order Confirmation",
                    color = Color.White, fontWeight = FontWeight.Bold, fontSize = 20.sp,
                    textAlign = TextAlign.Center, modifier = Modifier.weight(1f)
                )
                Spacer(modifier = Modifier.size(24.dp))
            }
        },
        bottomBar = {
            Row(
                modifier = Modifier.fillMaxWidth().background(Color.White)
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text("Total Price", color = Color.Gray, fontSize = 14.sp)
                    Text(
                        "RM %.2f".format(finalTotal),
                        fontWeight = FontWeight.Bold,
                        fontSize = 22.sp
                    )
                }
                Button(
                    onClick = {
                        if (defaultAddress == null) {
                            //if not, show an error message and do nothing.
                            Toast.makeText(
                                context,
                                "Please set a default delivery address first.",
                                Toast.LENGTH_LONG
                            ).show()
                        } else {
                            //if an address exists, proceed with payment.
                            paymentViewModel.saveDefaultPaymentChoice(
                                paymentType = selectedPaymentType,
                                selectedCard = selectedCard,
                                selectedBank = currentSelectedBank
                            )
                            navController.navigate("payment_successful")
                        }
                    },
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF29475F)),
                    modifier = Modifier.padding(start = 16.dp)
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.pay),
                        contentDescription = "Pay",
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.size(ButtonDefaults.IconSpacing))
                    Text("Pay Now", modifier = Modifier.padding(vertical = 8.dp), fontSize = 16.sp)
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding).fillMaxSize().verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column(Modifier.padding(16.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Delivery Address", fontWeight = FontWeight.Bold)
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Edit Address",
                            modifier = Modifier
                                .size(20.dp)
                                .clickable {
                                    //navigate to the new address management screen
                                    navController.navigate("address_screen")
                                }
                        )
                    }
                    Divider(modifier = Modifier.padding(vertical = 8.dp))

                    if (defaultAddress != null) {
                        //display the address from the Room database
                        Text(userProfile?.username ?: "Your Name", fontWeight = FontWeight.SemiBold)
                        Text(defaultAddress.addressLine1)
                        Text("${defaultAddress.postcode} ${defaultAddress.city}")
                        Text(defaultAddress.state)
                    } else {
                        //show a message if no default address is set
                        Text(
                            text = "No default address set. Please add one.",
                            color = Color.Red,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { navController.navigate("address_screen") }
                                .padding(vertical = 8.dp),
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
            Column(
                modifier = Modifier.fillMaxWidth().background(Color.White, shape = RoundedCornerShape(12.dp)).padding(16.dp)
            ) {
                PaymentOption(
                    title = "Credit Card",
                    isSelected = selectedPaymentType == "Credit Card",
                    onSelected = { selectedPaymentType = "Credit Card" },
                    savedCards = savedCards,
                    selectedCard = selectedCard,
                    onCardSelected = { selectedCard = it },
                    onEditClicked = { navController.navigate("add_credit_card") }
                )
                Divider(modifier = Modifier.padding(horizontal = 8.dp))
                PaymentOption(
                    title = "Online Banking",
                    isSelected = selectedPaymentType == "Online Banking",
                    onSelected = { selectedPaymentType = "Online Banking" },
                    //pass down the current bank and the callback to update it
                    initialBank = currentSelectedBank,
                    onBankSelected = { newBank ->
                        currentSelectedBank = newBank
                    }
                )
                Divider(modifier = Modifier.padding(horizontal = 8.dp))
                PaymentOption(
                    title = "Cash on Delivery",
                    isSelected = selectedPaymentType == "Cash",
                    onSelected = { selectedPaymentType = "Cash" }
                )
            }

            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth().background(Color.White, shape = RoundedCornerShape(12.dp)).padding(16.dp)
            ) {
                PriceRow("Subtotal", subtotal)
                PriceRow("Tax (10%)", tax)
                PriceRow("Delivery fee", deliveryFee)
            }
        }
    }
}

@Composable
fun PriceRow(label: String, amount: Double) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, color = Color.Gray)
        Text("RM%.2f".format(amount))
    }
}

@Composable
fun PaymentOption(
    title: String,
    isSelected: Boolean,
    onSelected: () -> Unit,
    savedCards: List<CreditCardEntity> = emptyList(),
    selectedCard: CreditCardEntity? = null,
    onCardSelected: (CreditCardEntity) -> Unit = {},
    onEditClicked: () -> Unit = {},
    initialBank: String = "Maybank",
    onBankSelected: (String) -> Unit = {},
    paymentViewModel: CreditCardViewModel? = null,
) {
    var bankMenuExpanded by remember { mutableStateOf(false) }
    val banks = listOf("Maybank", "CIMB", "Public Bank", "Hong Leong Bank")
    var selectedBank by remember(initialBank) { mutableStateOf(initialBank) }
    var cardMenuExpanded by remember { mutableStateOf(false) }

    //main row for the payment option
    Row(
        modifier = Modifier.fillMaxWidth().clickable(onClick = onSelected).padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        RadioButton(
            selected = isSelected,
            onClick = onSelected,
            colors = RadioButtonDefaults.colors(selectedColor = Color(0xFF29475F))
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(title, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))

        when (title) {
            "Credit Card" -> {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Image(painter = painterResource(id = R.drawable.visa), contentDescription = "Visa", modifier = Modifier.height(20.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Image(painter = painterResource(id = R.drawable.mastercard), contentDescription = "Mastercard", modifier = Modifier.height(20.dp))
                    Box {
                        IconButton(onClick = { if (savedCards.isNotEmpty()) cardMenuExpanded = true }) {
                            Icon(Icons.Default.ArrowDropDown, contentDescription = "Select Card")
                        }
                        DropdownMenu(expanded = cardMenuExpanded, onDismissRequest = { cardMenuExpanded = false }) {
                            savedCards.forEach { card ->
                                DropdownMenuItem(
                                    text = { Text(".... ${card.cardNumberLast4}") },
                                    onClick = {
                                        onCardSelected(card)
                                        cardMenuExpanded = false
                                    }
                                )
                            }
                        }
                    }
                }
            }
            "Online Banking" -> {
                Image(painter = painterResource(id = R.drawable.fpx), contentDescription = "FPX", modifier = Modifier.height(20.dp))
                Box {
                    IconButton(onClick = { bankMenuExpanded = true }) {
                        Icon(Icons.Default.ArrowDropDown, contentDescription = "Select Bank")
                    }
                    DropdownMenu(expanded = bankMenuExpanded, onDismissRequest = { bankMenuExpanded = false }) {
                        banks.forEach { bank ->
                            DropdownMenuItem(
                                text = { Text(bank) },
                                onClick = {
                                    selectedBank = bank
                                    onBankSelected(bank)
                                    bankMenuExpanded = false
                                }
                            )
                        }
                    }
                }
            }
            "Cash on Delivery" -> {
                Image(
                    painter = painterResource(id = R.drawable.cash),
                    contentDescription = "Cash on Delivery",
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }

    if (isSelected) {
        Row {
            Spacer(modifier = Modifier.width(48.dp))
            Column {
                if (title == "Credit Card") {
                    TextButton(onClick = onEditClicked, contentPadding = PaddingValues(0.dp)) {
                        Text("Edit Credit Card", fontSize = 12.sp)
                    }
                    Text(
                        if (selectedCard != null) ".... ${selectedCard.cardNumberLast4}" else "No card selected. Please add one.",
                        color = Color.Gray, fontSize = 12.sp,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                }
                if (title == "Online Banking") {
                    Text(
                        selectedBank,
                        color = Color.Gray, fontSize = 12.sp,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                }
            }
        }
    }
}