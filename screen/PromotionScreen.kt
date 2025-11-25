package com.example.assignmentexample.screens.promotion

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.assignmentexample.R
import com.example.assignmentexample.data.Promotion.PromotionViewModel
import com.example.assignmentexample.data.Reward.RewardTransaction
import com.example.assignmentexample.data.UserPoints.UserPoints
import com.example.assignmentexample.data.UserViewModel
import com.example.assignmentexample.navigation.BottomNavigationBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PromotionScreen(
    navController: NavController,
    userViewModel: UserViewModel,
    promotionViewModel: PromotionViewModel = viewModel()
) {
    val userPoints by promotionViewModel.userPoints.collectAsState()
    val transactions by promotionViewModel.rewardTransactions.collectAsState()
    val userProfile by userViewModel.userProfile.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Promotion", color = Color.White) },
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
        LazyColumn(
            modifier = Modifier.padding(paddingValues).fillMaxSize().background(Color(0xFFF0F2F5)),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item { LoyaltyCard(user = userPoints) }
            item { MyPointsCard(user = userPoints, onRedeemClicked = { navController.navigate("redeem_menu") }) }
            item {
                if (transactions.isNotEmpty()) {
                    HistoryRewards(transactions = transactions)
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
                        com.example.assignmentexample.screens.main.LoyaltyCup(isStamped = i <= completedStamps)
                    }
                }
            }
        }
    }
}

@Composable
fun LoyaltyCup(isStamped: Boolean) {
    val cupImage: Painter = if (isStamped) painterResource(id = R.drawable.filledcup)
    else painterResource(id = R.drawable.unfilledcup)
    val tint = if (isStamped) Color(0xFF324A59) else Color(0xFFB0BEC5).copy(alpha = 0.5f)
    Icon(painter = cupImage, contentDescription = "Cup", modifier = Modifier.size(30.dp), tint = tint)
}

@Composable
fun MyPointsCard(user: UserPoints?, onRedeemClicked: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF3B4B59))
    ) {
        Row(
            modifier = Modifier.padding(16.dp).fillMaxWidth(),
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

@Composable
fun HistoryRewards(transactions: List<RewardTransaction>) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            "History Rewards",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF2D3B47),
            modifier = Modifier.padding(bottom = 8.dp)
        )
        Column(modifier = Modifier.clip(RoundedCornerShape(16.dp)).background(Color.White)) {
            transactions.forEachIndexed { index, transaction ->
                RewardHistoryItem(transaction)
                if (index < transactions.size - 1) {
                    HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp), color = Color(0xFFF0F2F5))
                }
            }
        }
    }
}

@Composable
fun RewardHistoryItem(transaction: RewardTransaction) {
    val pointsText = if (transaction.pointsChange >= 0) "+ ${transaction.pointsChange} Pts"
    else "- ${kotlin.math.abs(transaction.pointsChange)} Pts"
    val pointsColor = if (transaction.pointsChange >= 0) Color(0xFF4DB6AC) else Color(0xFFE57373)

    Row(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column {
            Text(transaction.rewardName, fontWeight = FontWeight.SemiBold, color = Color(0xFF2D3B47))
            Spacer(modifier = Modifier.height(4.dp))
            Text(transaction.getFormattedDate(), fontSize = 12.sp, color = Color.Gray)
        }
        Text(pointsText, color = pointsColor, fontWeight = FontWeight.Bold)
    }
}