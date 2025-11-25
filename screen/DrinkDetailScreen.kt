package com.example.assignmentexample.screen

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
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
import androidx.navigation.NavHostController
import com.example.assignmentexample.R
import com.example.assignmentexample.data.Cart.CartItemEntity
import com.example.assignmentexample.data.Cart.CartViewModel
import com.example.assignmentexample.data.Drink.DrinkDetailViewModel
import com.example.assignmentexample.navigation.BottomNavigationBar

@Composable
fun DrinkDetailScreen(
    navController: NavController,
    drinkDetailViewModel: DrinkDetailViewModel,
    cartViewModel: CartViewModel
) {
    val drink by drinkDetailViewModel.drink.collectAsState()
    val context = LocalContext.current

    drink?.let { drinkEntity ->

        var quantity by remember { mutableStateOf(1) }
        var selectedSweetness by remember { mutableStateOf(1) } // 0:less, 1: Medium, 2: Extra
        var selectedSize by remember { mutableStateOf(1) }      // 0:less, 1: Medium, 2: Large
        var selectedIce by remember { mutableStateOf(1) }       // 0:less, 1: Normal ice, 2: Extra

        val sizePrice = when (selectedSize) {
            0 -> drinkEntity.price - 1.00 //small is RM1.00 cheaper
            1 -> drinkEntity.price         //medium is the base price
            2 -> drinkEntity.price + 1.00 //large is RM1.00 more expensive
            else -> drinkEntity.price
        }
        val total = quantity * sizePrice

        Scaffold(
            bottomBar = { BottomNavigationBar(navController = navController) }
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .verticalScroll(rememberScrollState())
                    .background(Color(0xFFF5F5F5))
            ) {
                // Top bar with Back Arrow and Cart Icon
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
                        text = "Details",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        modifier = Modifier.weight(1f),
                        textAlign = TextAlign.Center
                    )
                    Icon(
                        imageVector = Icons.Default.ShoppingCart,
                        contentDescription = "Cart",
                        tint = Color.White,
                        modifier = Modifier
                            .size(24.dp)
                            .clickable { navController.navigate("cart") }
                    )
                }

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

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Quantity", fontSize = 18.sp, fontWeight = FontWeight.SemiBold)
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.background(Color.LightGray.copy(alpha=0.3f), shape= RoundedCornerShape(50))
                        ) {
                            Text(" - ", fontSize = 24.sp, modifier = Modifier.clickable { if (quantity > 1) quantity-- }.padding(horizontal = 16.dp, vertical = 4.dp))
                            Text(quantity.toString(), fontSize = 18.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 4.dp))
                            Text(" + ", fontSize = 24.sp, modifier = Modifier.clickable { quantity++ }.padding(horizontal = 16.dp, vertical = 4.dp))
                        }
                    }

                    Divider(color = Color.LightGray.copy(alpha = 0.5f), modifier = Modifier.padding(vertical = 16.dp))

                    SweetnessSelector(selectedSweetness) { selectedSweetness = it }
                    Divider(color = Color.LightGray.copy(alpha = 0.5f), modifier = Modifier.padding(vertical = 16.dp))
                    SizeSelector(selectedSize) { selectedSize = it }
                    Divider(color = Color.LightGray.copy(alpha = 0.5f), modifier = Modifier.padding(vertical = 16.dp))
                    IceSelector(selectedIce) { selectedIce = it }

                    Spacer(modifier = Modifier.height(24.dp))

                    //add to Cart
                    Button(
                        onClick = {
                            val cartItem = CartItemEntity(
                                userOwnerId = "",
                                drinkId = drinkEntity.drinkId,
                                quantity = quantity,
                                sweetness = when (selectedSweetness) {
                                    0 -> "Less Sweet"
                                    1 -> "Normal Sweet"
                                    else -> "Extra Sweet"
                                },
                                size = when (selectedSize) {
                                    0 -> "Small Size"
                                    1 -> "Medium Size"
                                    else -> "Large Size"
                                },
                                ice = when (selectedIce) {
                                    0 -> "Less Ice"
                                    1 -> "Normal Ice"
                                    else -> "Extra Ice"
                                },
                                totalPrice = total
                            )
                            cartViewModel.addItem(cartItem)
                            Toast.makeText(context, "${drinkEntity.name} added to cart!", Toast.LENGTH_SHORT).show()
                            navController.popBackStack()
                        },
                        modifier = Modifier.fillMaxWidth().height(50.dp),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Add to Cart - RM %.2f".format(total), fontSize = 18.sp)
                    }
                }
            }
        }
    } ?: Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        CircularProgressIndicator()
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