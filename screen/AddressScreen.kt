package com.example.assignmentexample.screen

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.assignmentexample.data.Address.AddressEntity
import com.example.assignmentexample.data.Address.AddressViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddressScreen(navController: NavController, addressViewModel: AddressViewModel) {
    val addresses by addressViewModel.allAddresses.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
            title = { Text("Address ", color = Color.White) },
            navigationIcon = {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back", tint = Color.White)
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF2D3B47))
        ) },
        floatingActionButton = {
            FloatingActionButton(onClick = { navController.navigate("add_address") }) {
                Icon(Icons.Default.Add, "Add Address")
            }
        }
    ) { padding ->
        LazyColumn(contentPadding = padding, modifier = Modifier.padding(16.dp)) {
            items(addresses) { address ->
                AddressCard(
                    address = address,
                    onDelete = { addressViewModel.deleteAddress(address) },
                    onSetDefault = { addressViewModel.setDefaultAddress(address) }
                )
            }
        }
    }
}

@Composable
fun AddressCard(address: AddressEntity, onDelete: () -> Unit, onSetDefault: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp)
            .border(
                width = if (address.isDefault) 2.dp else 0.dp,
                color = if (address.isDefault) Color(0xFF2D3B47) else Color.Transparent,
                shape = RoundedCornerShape(12.dp)
            ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(Modifier.padding(16.dp)) {
            Text("${address.addressLine1}, ${address.city}", fontWeight = FontWeight.Bold)
            Text("${address.postcode} ${address.state}")
            if (address.isDefault) {
                Text("Default", color = Color.Green, fontWeight = FontWeight.Bold)
            }
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                if (!address.isDefault) {
                    TextButton(onClick = onSetDefault) { Text("Set as Default") }
                }
                IconButton(onClick = onDelete) { Icon(Icons.Default.Delete, "Delete") }
            }
        }
    }
}