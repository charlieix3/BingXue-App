package com.example.assignmentexample.screens.account

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.assignmentexample.R
import com.example.assignmentexample.data.History.OrderHistoryViewModel
import com.example.assignmentexample.data.Promotion.PromotionViewModel
import com.example.assignmentexample.data.UserViewModel
import com.example.assignmentexample.navigation.BottomNavigationBar


private val AccountDarkBlueGray = Color(0xFF2D3B47)
private val AccountLightGrayBackground = Color(0xFFF0F2F5)
private val AccountCardBackground = Color(0xFFFFFFFF)
private val AccountTextColorPrimary = Color.White
private val AccountTextColorDark = Color(0xFF2D3B47)

@Composable
fun AccountScreen(
    navController: NavController,
    userViewModel: UserViewModel,
    promotionViewModel: PromotionViewModel,
    orderHistoryViewModel: OrderHistoryViewModel
) {
    val userProfile by userViewModel.userProfile.collectAsState()
    val userPoints by promotionViewModel.userPoints.collectAsState()
    val orderedItemsCount by orderHistoryViewModel.totalOrderedItemsCount.collectAsState()



    Scaffold(
        containerColor = AccountLightGrayBackground,

        topBar = {
            AccountTopAppBar(
                navController = navController,
                username = userProfile?.username ?: "Guest",
                onProfileClick = { navController.navigate("edit_profile") }
            )
        },
        bottomBar = { BottomNavigationBar(navController = navController) }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            item {
                MyAccountSection(
                    points = userPoints?.points ?: 0,
                    orderedItemsCount = orderedItemsCount,
                    onPointsClicked = { navController.navigate("promotion") },
                    onOrderedItemsClicked = { navController.navigate("order_history") }
                )
            }
            item {
                AccountMenuList(
                    navController = navController,
                    onLogoutClick = {
                        userViewModel.logout()
                        navController.navigate("welcome") {
                            popUpTo(navController.graph.id) { inclusive = true }
                        }
                    }
                )
            }
            item { GrandOpeningBanner() }
        }
    }
}

@Composable
fun AccountTopAppBar(
    navController: NavController,
    username: String,
    onProfileClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(80.dp)
            .clickable(onClick = onProfileClick)
            .background(
                AccountDarkBlueGray,
                shape = RoundedCornerShape(bottomStart = 20.dp, bottomEnd = 20.dp)
            )
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {

        Row(verticalAlignment = Alignment.CenterVertically) {
            Image(
                painter = painterResource(id = R.drawable.pfp),
                contentDescription = "Profile Picture",
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = username, //use dynamic username
                color = AccountTextColorPrimary,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
        }
        IconButton(onClick = { navController.navigate("cart") }) {
            Icon(
                Icons.Default.ShoppingCart,
                contentDescription = "Shopping Cart",
                tint = AccountTextColorPrimary
            )
        }
    }
}

@Composable
fun MyAccountSection(
    points: Int,
    orderedItemsCount: Int,
    onPointsClicked: () -> Unit,
    onOrderedItemsClicked: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(AccountDarkBlueGray)
            .padding(16.dp)
    ) {
        Text(
            text = "My Account",
            color = Color.White,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            AccountInfoCard(
                label = "Ordered Items",
                value = orderedItemsCount.toString(), //use the dynamic count
                iconRes = R.drawable.ordered_items_icon,
                modifier = Modifier
                    .weight(1f)
                    .clickable { onOrderedItemsClicked() }
            )
            AccountInfoCard(
                label = "Points",
                value = points.toString(),
                iconRes = R.drawable.points_icon,
                modifier = Modifier
                    .weight(1f)
                    .clickable { onPointsClicked() }
            )
        }
    }
}


@Composable
fun AccountInfoCard(label: String, value: String, iconRes: Int, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(AccountCardBackground)
            .padding(12.dp),
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = value,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = AccountTextColorDark
            )
            Icon(
                painter = painterResource(id = iconRes),
                contentDescription = label,
                tint = AccountTextColorDark.copy(alpha = 0.7f)
            )
        }
        Text(
            text = label,
            fontSize = 12.sp,
            color = Color.Gray
        )
    }
}

@Composable
fun AccountMenuList(navController: NavController, onLogoutClick: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(AccountCardBackground)
    ) {
        AccountMenuItem(
            iconRes = R.drawable.order_history_icon,
            text = "Order History",
            onClick = { navController.navigate("order_history") }
        )
        Divider(modifier = Modifier.padding(horizontal = 16.dp), color = AccountLightGrayBackground)

        AccountMenuItem(
            iconRes = R.drawable.payment,
            text = "Payment Method",
            onClick = { navController.navigate("payment_methods") }
        )

        Divider(modifier = Modifier.padding(horizontal = 16.dp), color = AccountLightGrayBackground)

        AccountMenuItem(
            iconRes = R.drawable.address,
            text = "My Addresses",
            onClick = { navController.navigate("address_screen") }
        )

        Divider(modifier = Modifier.padding(horizontal = 16.dp), color = AccountLightGrayBackground)

        AccountMenuItem(
            iconRes = R.drawable.faq,
            text = "FAQ",
            onClick = { navController.navigate("faq") }
        )
        Divider(modifier = Modifier.padding(horizontal = 16.dp), color = AccountLightGrayBackground)
        AccountMenuItem(
            iconRes = R.drawable.logout,
            text = "Logout",
            onClick = onLogoutClick
        )
    }
}

@Composable
fun AccountMenuItem(
    iconRes: Int,
    text: String,
    onClick: () -> Unit = {}
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                painter = painterResource(id = iconRes),
                contentDescription = text,
                tint = AccountTextColorDark,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Text(text = text, color = AccountTextColorDark, fontWeight = FontWeight.SemiBold)
        }
        Icon(
            painter = painterResource(id = R.drawable.right_arrow_icon),
            contentDescription = "Go to $text",
            tint = Color.Gray,
            modifier = Modifier.size(16.dp)
        )
    }
}

@Composable
fun GrandOpeningBanner() {
    val context = LocalContext.current
    val url = "https://www.bingxueglobal.com/contact/"

    Image(
        painter = painterResource(id = R.drawable.grand_opening_banner),
        contentDescription = "Grand Opening Banner",
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .clickable {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                context.startActivity(intent)
            },
        contentScale = ContentScale.Crop
    )

}