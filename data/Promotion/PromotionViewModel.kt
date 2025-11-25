package com.example.assignmentexample.data.Promotion

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.assignmentexample.data.AppDatabase
import com.example.assignmentexample.data.Drink.DrinkEntity
import com.example.assignmentexample.data.History.OrderHistoryEntity
import com.example.assignmentexample.data.UserPoints.UserPoints
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import com.example.assignmentexample.data.Reward.RewardTransaction
import com.example.assignmentexample.data.SettingsManager
import com.example.assignmentexample.data.UserViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf


class PromotionViewModel(
    application: Application,
    private val userViewModel: UserViewModel
) : AndroidViewModel(application) {

    private val settingsManager = SettingsManager(application)
    private val userPointsDao = AppDatabase.getDatabase(application).userPointsDao()
    private val orderHistoryDao = AppDatabase.getDatabase(application).orderHistoryDao()
    private val rewardTransactionDao = AppDatabase.getDatabase(application).rewardTransactionDao()


    val userPoints: StateFlow<UserPoints?> = userViewModel.userProfile
        .flatMapLatest { user ->
            if (user == null) flowOf(null) else userPointsDao.getUserPoints(user.uid)
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)


    val rewardTransactions: StateFlow<List<RewardTransaction>> = userViewModel.userProfile
        .flatMapLatest { user ->
            if (user == null) flowOf(emptyList()) else rewardTransactionDao.getAllTransactions(user.uid)
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())



    fun redeemDrink(drink: DrinkEntity, details: String) {
        viewModelScope.launch {
            val userId = userViewModel.userProfile.value?.uid ?: return@launch
            val currentUserPoints = userPoints.value ?: return@launch

            val pointsToDeduct = drink.pointsValue

            if (currentUserPoints.points >= pointsToDeduct) {
                //subtract points from the current user
                val updatedPoints = currentUserPoints.copy(points = currentUserPoints.points - pointsToDeduct)
                userPointsDao.upsertUser(updatedPoints)


                rewardTransactionDao.insertTransaction(
                    RewardTransaction(
                        userOwnerId = userId,
                        rewardName = "Redeemed: ${drink.name}",
                        pointsChange = -pointsToDeduct
                    )
                )

                //create order history record
                val newOrderIdNumber = settingsManager.lastOrderId + 1
                settingsManager.lastOrderId = newOrderIdNumber
                val formattedOrderId = "ORD$newOrderIdNumber"

                orderHistoryDao.insertOrder(
                    OrderHistoryEntity(
                        userOwnerId = userId,
                        orderId = formattedOrderId,
                        drinkId = drink.drinkId,
                        details = details,
                        quantity = 1,
                        price = 0.0,
                        orderDate = System.currentTimeMillis(),
                        paymentMethod = "Paid with Points"
                    )
                )
            }
        }
    }
}