package com.example.assignmentexample.screens.main

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.assignmentexample.R
import com.example.assignmentexample.data.Promotion.PromotionViewModel
import com.example.assignmentexample.data.UserPoints.UserPoints
import com.example.assignmentexample.data.UserViewModel
import com.example.assignmentexample.navigation.BottomNavigationBar
import com.example.assignmentexample.navigation.Screen

//make ads horizontally scrollable
data class AdBanner(
    val imageRes: Int,
    val contentDescription: String
)

val adBanners = listOf(
    AdBanner(R.drawable.ad, "Summer Sale"),
    AdBanner(R.drawable.ad2, "New Drink Promo"),
    AdBanner(R.drawable.ad3, "Grand Opening")
)

@Composable
fun HomeScreen(
    navController: NavController,
    userViewModel: UserViewModel,
    promotionViewModel: PromotionViewModel
) {
    val userProfile by userViewModel.userProfile.collectAsState()
    val userPoints by promotionViewModel.userPoints.collectAsState()

    Scaffold(
        topBar = {
            HomeTopAppBar(
                username = userProfile?.username ?: "Guest",
                onProfileClick = { navController.navigate(Screen.Account.route) },
                onCartClick = { navController.navigate("cart") }
            )
        },
        bottomBar = { BottomNavigationBar(navController = navController) }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .background(Color(0xFFF0F2F5)),
            verticalArrangement = Arrangement.spacedBy(20.dp),
            contentPadding = PaddingValues(top = 20.dp, bottom = 20.dp)
        ) {
            //ads Section
            item {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    contentPadding = PaddingValues(horizontal = 16.dp)
                ) {
                    items(adBanners) { banner ->
                        Image(
                            painter = painterResource(id = banner.imageRes),
                            contentDescription = banner.contentDescription,
                            modifier = Modifier
                                .width(300.dp)
                                .height(300.dp)
                                .clip(RoundedCornerShape(16.dp))
                                .clickable {
                                    navController.navigate(Screen.Menu.route)
                                },
                            contentScale = ContentScale.Crop
                        )
                    }
                }
            }

            //loyalty card Section
            item {
                Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                    LoyaltyCard(user = userPoints)
                }
            }

            //points redeem Section
            item {
                Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                    MyPointsCard(
                        user = userPoints,
                        onRedeemClicked = { navController.navigate(Screen.Promotion.route) }
                    )
                }
            }
        }
    }
}


@Composable
fun LoyaltyCard(user: UserPoints?) {
    val completedStamps = user?.loyaltyStamps ?: 0
    val totalStamps = 8

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF3B4B59))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Loyalty card", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                Text("$completedStamps / $totalStamps", color = Color(0xFFB0BEC5), fontSize = 14.sp)
            }
            Spacer(modifier = Modifier.height(16.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color.White)
                    .padding(vertical = 12.dp, horizontal = 8.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceAround,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    for (i in 1..totalStamps) {
                        LoyaltyCup(isStamped = i <= completedStamps)
                    }
                }
            }
        }
    }
}

//loyalty cup
@Composable
fun LoyaltyCup(isStamped: Boolean) {
    val cupImage: Painter = if (isStamped) painterResource(id = R.drawable.filledcup)
    else painterResource(id = R.drawable.unfilledcup)
    val tint = if (isStamped) Color(0xFF324A59) else Color(0xFFB0BEC5).copy(alpha = 0.5f)
    Icon(painter = cupImage, contentDescription = "Cup", modifier = Modifier.size(30.dp), tint = tint)
}


// point card
@Composable
fun MyPointsCard(user: UserPoints?, onRedeemClicked: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF3B4B59))
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(painter = painterResource(id = R.drawable.points_icon), "Points", tint = Color(0xFF4DB6AC), modifier = Modifier.size(50.dp))
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Text("My Points:", color = Color(0xFFB0BEC5), fontSize = 14.sp)
                    Text("${user?.points ?: 0}", color = Color.White, fontSize = 28.sp, fontWeight = FontWeight.Bold)
                }
            }
            Button(
                onClick = onRedeemClicked,
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4DB6AC))
            ) {
                Text("Redeem drinks", color = Color.White)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeTopAppBar(
    username: String,
    onProfileClick: () -> Unit,
    onCartClick: () -> Unit
) {
    TopAppBar(
        title = {
            Column {
                Text("Welcome back,", color = Color.White.copy(alpha = 0.8f), fontSize = 14.sp)
                Text(
                    text = username,
                    color = Color.White,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        },
        navigationIcon = {
            Image(
                painter = painterResource(id = R.drawable.pfp),
                contentDescription = "Profile Picture",
                modifier = Modifier
                    .padding(start = 12.dp)
                    .size(48.dp)
                    .clip(CircleShape)
                    .clickable(onClick = onProfileClick)
            )
        },
        actions = {
            IconButton(onClick = onCartClick) {
                Icon(
                    imageVector = Icons.Default.ShoppingCart,
                    contentDescription = "Cart",
                    tint = Color.White,
                    modifier = Modifier.size(28.dp)
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = Color(0xFF2D3B47)
        )
    )
}