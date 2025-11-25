package com.example.assignmentexample.screen

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import com.example.assignmentexample.data.Promotion.PromotionViewModel
import com.example.assignmentexample.data.SharedPrefHelper


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RedeemDrinkMenuScreen(
    navController: NavController,
    drinkMenuViewModel: DrinkMenuViewModel = viewModel(),
    promotionViewModel: PromotionViewModel = viewModel()
) {
    //get the list of drinks from the database
    val drinksFromDb by drinkMenuViewModel.drinks.collectAsState(initial = emptyList())
    //get the user current points
    val userPoints by promotionViewModel.userPoints.collectAsState()
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
                title = { Text("Redeem a Drink", color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF2D3B47))
            )
        }
    ) { paddingValues ->
        Column(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
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

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(filteredDrinks) { drink ->
                    Box(modifier = Modifier.clickable {
                        if ((userPoints?.points ?: 0) >= drink.pointsValue) {
                            navController.navigate("redeem_detail/${drink.drinkId}")
                        } else {
                            Toast.makeText(context, "Not enough points to redeem!", Toast.LENGTH_SHORT).show()
                        }
                    }) {
                        RedeemDrinkItemCard(drink = drink)
                    }
                }
            }
        }
    }
}


@Composable
fun RedeemDrinkItemCard(drink: DrinkEntity) {
    Card(
        modifier = Modifier.fillMaxWidth(),
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
                //display the points value from the database
                Text(
                    text = "${drink.pointsValue} Pts",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF4DB6AC)
                )
            }
        }
    }
}

