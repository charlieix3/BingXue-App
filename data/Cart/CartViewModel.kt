package com.example.assignmentexample.data.Cart

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.assignmentexample.data.AppDatabase
import com.example.assignmentexample.data.Drink.DrinkEntity
import com.example.assignmentexample.data.UserViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

//Cart display item data class
data class CartDisplayItem(
    val cartItem: CartItemEntity,
    val drink: DrinkEntity
)


class CartViewModel(
    application: Application,
    private val cartRepository: CartRepository,
    private val userViewModel: UserViewModel
) : AndroidViewModel(application) {

    private val drinkDao = AppDatabase.getDatabase(application).drinkDao()

    val cartDisplayItems: StateFlow<List<CartDisplayItem>> = userViewModel.userProfile
        .flatMapLatest { user ->
            if (user == null) {
                flowOf(emptyList())
            } else {
                cartRepository.getAllItemsForUser(user.uid)
                    .combine(drinkDao.getAllDrinks()) { cartEntities, drinks ->
                        cartEntities.mapNotNull { cartEntity ->
                            drinks.find { it.drinkId == cartEntity.drinkId }?.let { drinkEntity ->
                                CartDisplayItem(cartItem = cartEntity, drink = drinkEntity)
                            }
                        }
                    }
            }
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun addItem(item: CartItemEntity) {
        viewModelScope.launch {
            val userId = userViewModel.userProfile.value?.uid ?: return@launch
            cartRepository.addItem(item.copy(userOwnerId = userId))
        }
    }

    fun removeItem(item: CartItemEntity) {
        viewModelScope.launch {
            cartRepository.removeItem(item)
        }
    }

    fun clearCart() {
        viewModelScope.launch {
            val userId = userViewModel.userProfile.value?.uid ?: return@launch
            cartRepository.clearCartForUser(userId)
        }
    }
}


class CartViewModelFactory(
    private val repository: CartRepository,
    private val application: Application,
    private val userViewModel: UserViewModel
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CartViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return CartViewModel(application, repository, userViewModel) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}