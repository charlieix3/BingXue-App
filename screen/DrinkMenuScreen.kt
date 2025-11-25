package com.example.assignmentexample.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.assignmentexample.data.Drink.DrinkEntity
import com.example.assignmentexample.data.Drink.DrinkMenuViewModel
import com.example.assignmentexample.data.SharedPrefHelper
import com.example.assignmentexample.data.UserViewModel
import com.example.assignmentexample.navigation.BottomNavigationBar

@Composable
fun PreferenceButtons(
    categories: List<String>,
    selectedCategory: String,
    onCategorySelected: (String) -> Unit
) {
    LazyRow(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(categories) { category ->
            Box(
                modifier = Modifier
                    .background(
                        color = if (category == selectedCategory) Color(0xFF29475F) else Color.White,
                        shape = RoundedCornerShape(16.dp)
                    )
                    .border(
                        width = 1.dp,
                        color = Color(0xFF29475F),
                        shape = RoundedCornerShape(16.dp)
                    )
                    .clickable { onCategorySelected(category) }
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = category,
                    color = if (category == selectedCategory) Color.White else Color(0xFF29475F),
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DrinkMenuScreen(
    navController: NavController,
    userViewModel: UserViewModel,
    drinkMenuViewModel: DrinkMenuViewModel = viewModel()
) {
    val drinksFromDb by drinkMenuViewModel.drinks.collectAsState(initial = emptyList())
    val userProfile by userViewModel.userProfile.collectAsState()

    val context = LocalContext.current
    val prefHelper = remember { SharedPrefHelper(context) }
    var selectedCategory by remember { mutableStateOf(prefHelper.getCategory()) }



    val filteredDrinks = if (selectedCategory == "All") {
        drinksFromDb
    } else {
        drinksFromDb.filter { it.category == selectedCategory }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Menu", color = Color.White) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF2D3B47)),
                actions = {
                    IconButton(onClick = { navController.navigate("cart") }) {
                        Icon(
                            imageVector = Icons.Default.ShoppingCart,
                            contentDescription = "My Cart",
                            tint = Color.White
                        )
                    }
                }
            )
        },
        bottomBar = { BottomNavigationBar(navController = navController) }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(Color(0xFFF0F2F5))
        ) {
            Spacer(modifier = Modifier.height(8.dp))

            PreferenceButtons(
                categories = listOf("All", "Sundae", "Bubble Tea", "Juice"),
                selectedCategory = selectedCategory,
                onCategorySelected = { category ->
                    selectedCategory = category
                    prefHelper.saveCategory(category)
                }
            )

            Spacer(modifier = Modifier.height(8.dp))

            DrinkList(navController = navController, drinks = filteredDrinks)
        }
    }
}

@Composable
fun DrinkList(navController: NavController, drinks: List<DrinkEntity>) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(
            items = drinks,
            key = { it.drinkId }
        ) { drink ->
            DrinkItem(
                drink = drink,
                onClick = { navController.navigate("detail/${drink.drinkId}") }
            )
        }
    }
}

@Composable
fun DrinkItem(drink: DrinkEntity, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF29475F))
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(id = drink.imageRes),
                contentDescription = drink.name,
                modifier = Modifier
                    .size(70.dp)
                    .padding(end = 12.dp),
                contentScale = ContentScale.Crop
            )
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    drink.name,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Text(drink.chinese, fontSize = 14.sp, color = Color.White.copy(alpha = 0.8f))
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    "RM %.2f".format(drink.price),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.White
                )
            }
        }
    }

}