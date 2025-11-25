package com.example.assignmentexample.data.History

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.assignmentexample.data.AppDatabase
import com.example.assignmentexample.data.Drink.DrinkEntity
import com.example.assignmentexample.data.SettingsManager
import com.example.assignmentexample.data.UserViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class OrderHistoryDisplayItem(
    val order: OrderHistoryEntity,
    val drink: DrinkEntity
)

class OrderHistoryViewModel(
    application: Application,
    private val userViewModel: UserViewModel
) : AndroidViewModel(application) {

    private val orderHistoryDao = AppDatabase.getDatabase(application).orderHistoryDao()
    private val drinkDao = AppDatabase.getDatabase(application).drinkDao()
    private val settingsManager = SettingsManager(application)

    private val _sortOption = MutableStateFlow(settingsManager.orderHistorySortOption)
    val sortOption: StateFlow<String> = _sortOption

    val orderHistoryItems: StateFlow<List<OrderHistoryDisplayItem>> = userViewModel.userProfile
        .flatMapLatest { user ->
            if (user == null) {
                flowOf(emptyList()) //if no user is logged in, return an empty list
            } else {
                //if a user is logged in, fetch their specific order history
                combine(
                    orderHistoryDao.getAllOrders(user.uid),
                    drinkDao.getAllDrinks(),
                    _sortOption
                ) { orders, drinks, sort ->
                    val displayItems = orders.mapNotNull { order ->
                        drinks.find { it.drinkId == order.drinkId }?.let { drink ->
                            OrderHistoryDisplayItem(order = order, drink = drink)
                        }
                    }
                    //apply sorting logic
                    when (sort) {
                        "Oldest" -> displayItems.sortedBy { it.order.orderDate }
                        "Most Expensive" -> displayItems.sortedByDescending { it.order.price }
                        "Cheapest" -> displayItems.sortedBy { it.order.price }
                        else -> displayItems
                    }
                }
            }
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val totalOrderedItemsCount: StateFlow<Int> = orderHistoryItems
        .map { it.size }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    fun updateSortOption(newSortOption: String) {
        settingsManager.orderHistorySortOption = newSortOption
        _sortOption.value = newSortOption
    }

    fun deleteOrder(order: OrderHistoryEntity) {
        viewModelScope.launch {
            orderHistoryDao.deleteOrder(order)
        }
    }
}