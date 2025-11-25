package com.example.assignmentexample.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.assignmentexample.R
import com.example.assignmentexample.data.Drink.DrinkDetailViewModel
import com.example.assignmentexample.data.Promotion.PromotionViewModel

@Composable
fun RedeemDrinkDetailScreen(
    navController: NavController,
    drinkDetailViewModel: DrinkDetailViewModel,
    promotionViewModel: PromotionViewModel
) {
    //get the drink details from the database through the ViewModel
    val drink by drinkDetailViewModel.drink.collectAsState()

    //show the screen only when the drink data has been loaded
    drink?.let { drinkEntity ->

        var selectedSweetness by remember { mutableStateOf(1) }
        var selectedSize by remember { mutableStateOf(1) }
        var selectedIce by remember { mutableStateOf(1) }

        val pointsForSize = when (selectedSize) {
            0 -> drinkEntity.pointsValue - 10 // 10 points cheaper
            1 -> drinkEntity.pointsValue         // base points
            2 -> drinkEntity.pointsValue + 10 // 10 points more expensive
            else -> drinkEntity.pointsValue
        }

        Scaffold { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .background(Color(0xFFF5F5F5))
            ) {
                RedeemTopBar(navController = navController)
                Spacer(modifier = Modifier.height(16.dp))
                Image(
                    painter = painterResource(id = drinkEntity.imageRes),
                    contentDescription = drinkEntity.name,
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .size(180.dp)
                )
                Spacer(modifier = Modifier.height(16.dp))

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .background(Color.White, RoundedCornerShape(16.dp))
                        .padding(16.dp)
                ) {
                    Text(drinkEntity.name, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(16.dp))

                    SweetnessSelector(selectedSweetness) { selectedSweetness = it }
                    Divider(modifier = Modifier.padding(vertical = 16.dp))
                    SizeSelector(selectedSize) { selectedSize = it }
                    Divider(modifier = Modifier.padding(vertical = 16.dp))
                    IceSelector(selectedIce) { selectedIce = it }
                }

                Spacer(Modifier.weight(1f))

                //redeem Button
                Button(
                    onClick = {
                        val details = "${when (selectedSize) {
                            0 -> "Small"
                            1 -> "Medium"
                            else -> "Large"
                        }} | ${when (selectedSweetness) {
                            0 -> "Less"
                            1 -> "Normal"
                            else -> "More"
                        }} | ${when (selectedIce) {
                            0 -> "None"
                            1 -> "Less"
                            else -> "Normal"
                        }}"

                        val updatedDrink = drinkEntity.copy(pointsValue = pointsForSize)
                        promotionViewModel.redeemDrink(updatedDrink, details)

                        navController.navigate("redeem_success") {
                            popUpTo("redeem_menu") { inclusive = true }
                        }
                    },
                    modifier = Modifier.fillMaxWidth().padding(16.dp).height(50.dp),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Redeem for $pointsForSize Pts", fontSize = 18.sp)
                }
            }
        }
    } ?: Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        CircularProgressIndicator()
    }
}



@Composable
private fun RedeemTopBar(navController: NavController) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFF29475F))
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
            contentDescription = "Back",
            tint = Color.White,
            modifier = Modifier
                .size(24.dp)
                .clickable { navController.navigateUp() }
        )
        Text(
            text = "Redeem Drink",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White,
            modifier = Modifier.weight(1f),
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.size(24.dp))
    }
}

@Composable
private fun SweetnessSelector(selectedIndex: Int, onSelect: (Int) -> Unit) {
    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
        Text("Sweetness", fontSize = 18.sp, fontWeight = FontWeight.Normal)
        Spacer(Modifier.weight(1f))
        Row(horizontalArrangement = Arrangement.spacedBy(24.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(painter = painterResource(id = R.drawable.sweetness_1), "Less", tint = if (selectedIndex == 0) Color.Black else Color.LightGray, modifier = Modifier.size(24.dp).clickable { onSelect(0) })
            Icon(painter = painterResource(id = R.drawable.sweetness_2), "Normal", tint = if (selectedIndex == 1) Color.Black else Color.LightGray, modifier = Modifier.size(32.dp).clickable { onSelect(1) })
            Icon(painter = painterResource(id = R.drawable.sweetness_3), "More", tint = if (selectedIndex == 2) Color.Black else Color.LightGray, modifier = Modifier.size(24.dp).clickable { onSelect(2) })
        }
    }
}

@Composable
private fun SizeSelector(selectedIndex: Int, onSelect: (Int) -> Unit) {
    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
        Text("Size", fontSize = 18.sp, fontWeight = FontWeight.Normal)
        Spacer(Modifier.weight(1f))
        Row(horizontalArrangement = Arrangement.spacedBy(24.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(painter = painterResource(id = R.drawable.size_1), "Small", tint = if (selectedIndex == 0) Color.Black else Color.LightGray, modifier = Modifier.size(28.dp).clickable { onSelect(0) })
            Icon(painter = painterResource(id = R.drawable.size_2), "Medium", tint = if (selectedIndex == 1) Color.Black else Color.LightGray, modifier = Modifier.size(36.dp).clickable { onSelect(1) })
            Icon(painter = painterResource(id = R.drawable.size_3), "Large", tint = if (selectedIndex == 2) Color.Black else Color.LightGray, modifier = Modifier.size(28.dp).clickable { onSelect(2) })
        }
    }
}

@Composable
private fun IceSelector(selectedIndex: Int, onSelect: (Int) -> Unit) {
    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
        Text("Ice", fontSize = 18.sp, fontWeight = FontWeight.Normal)
        Spacer(Modifier.weight(1f))
        Row(horizontalArrangement = Arrangement.spacedBy(24.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(painter = painterResource(id = R.drawable.ice_1), "No Ice", tint = if (selectedIndex == 0) Color.Black else Color.LightGray, modifier = Modifier.size(32.dp).clickable { onSelect(0) })
            Icon(painter = painterResource(id = R.drawable.ice_2), "Less Ice", tint = if (selectedIndex == 1) Color.Black else Color.LightGray, modifier = Modifier.size(32.dp).clickable { onSelect(1) })
            Icon(painter = painterResource(id = R.drawable.ice_3), "Normal Ice", tint = if (selectedIndex == 2) Color.Black else Color.LightGray, modifier = Modifier.size(32.dp).clickable { onSelect(2) })
        }
    }
}

