package com.example.assignmentexample.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.assignmentexample.data.History.OrderHistoryDisplayItem
import com.example.assignmentexample.data.History.OrderHistoryEntity
import com.example.assignmentexample.data.History.OrderHistoryViewModel
import com.example.assignmentexample.navigation.BottomNavigationBar


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrderHistoryScreen(
    navController: NavController,
    orderHistoryViewModel: OrderHistoryViewModel
) {
    val orderHistoryItems by orderHistoryViewModel.orderHistoryItems.collectAsState()
    val currentSortOption by orderHistoryViewModel.sortOption.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Order History", color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF2D3B47))
            )
        },
        bottomBar = { BottomNavigationBar(navController = navController) }
    ) { paddingValues ->
        Column(modifier = Modifier.padding(paddingValues).fillMaxSize()) {
            SortDropDown(
                selectedOption = currentSortOption,
                onOptionSelected = { newOption ->
                    orderHistoryViewModel.updateSortOption(newOption)
                },
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            )

            if (orderHistoryItems.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("You have no past orders.", color = Color.Gray)
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    val groupedOrders = orderHistoryItems.groupBy { it.order.orderId }
                    groupedOrders.forEach { (orderId, itemsInOrder) ->
                        val firstItem = itemsInOrder.first()
                        item {
                            OrderHeader(
                                orderId = firstItem.order.orderId,
                                orderDate = firstItem.order.getFormattedDate(),
                                paymentMethod = firstItem.order.paymentMethod
                            )
                        }
                        items(itemsInOrder) { item ->
                            OrderHistoryRow(
                                item = item,
                                onDelete = { orderHistoryViewModel.deleteOrder(item.order) }
                            )
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SortDropDown(
    selectedOption: String,
    onOptionSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val options = listOf("Newest", "Oldest", "Most Expensive", "Cheapest")
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded },
        modifier = modifier
    ) {
        OutlinedTextField(
            value = "Sort by: $selectedOption",
            onValueChange = {},
            readOnly = true,
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier.menuAnchor().fillMaxWidth(),
            colors = TextFieldDefaults.colors(
                unfocusedContainerColor = Color.Transparent,
                focusedContainerColor = Color.Transparent
            )
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = { Text(option) },
                    onClick = {
                        onOptionSelected(option)
                        expanded = false
                    }
                )
            }
        }
    }
}

@Composable
fun OrderHeader(orderId: String, orderDate: String, paymentMethod: String) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 8.dp, top = 16.dp)
    ) {
        Text(text = "Order #$orderId", fontWeight = FontWeight.Bold, fontSize = 18.sp)
        Text(text = orderDate, fontSize = 14.sp, color = Color.Gray)
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(text = "Paid with: ", fontSize = 14.sp, color = Color.Gray)
            Text(text = paymentMethod, fontSize = 14.sp, color = Color.Gray, fontWeight = FontWeight.SemiBold)
        }
        Divider(modifier = Modifier.padding(top = 8.dp))
    }
}


@Composable
fun OrderHistoryRow(item: OrderHistoryDisplayItem, onDelete: () -> Unit) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        Image(
            painter = painterResource(id = item.drink.imageRes),
            contentDescription = item.drink.name,
            contentScale = ContentScale.Crop,
            modifier = Modifier.size(64.dp).clip(RoundedCornerShape(8.dp))
        )
        Column(
            modifier = Modifier.weight(1f).padding(horizontal = 12.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(item.drink.name, fontWeight = FontWeight.Bold, fontSize = 16.sp)
            Text(text = item.order.details, fontSize = 12.sp, color = Color.Gray)
            Text("x ${item.order.quantity}", fontSize = 12.sp, color = Color.Gray)
        }
        Text(
            text = "RM %.2f".format(item.order.price),
            fontWeight = FontWeight.SemiBold,
            fontSize = 16.sp,
            textAlign = TextAlign.End
        )
        IconButton(onClick = onDelete) {
            Icon(imageVector = Icons.Default.Delete, contentDescription = "Remove Item", tint = Color.Red)
        }
    }
}

