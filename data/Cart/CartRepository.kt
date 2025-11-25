package com.example.assignmentexample.data.Cart

import kotlinx.coroutines.flow.Flow

class CartRepository(private val cartDao: CartDao) {

    fun getAllItemsForUser(userId: String): Flow<List<CartItemEntity>> {
        return cartDao.getAllItemsFlow(userId)
    }

    suspend fun addItem(item: CartItemEntity) = cartDao.insertCartItem(item)

    suspend fun removeItem(item: CartItemEntity) = cartDao.deleteCartItem(item)

    suspend fun clearCartForUser(userId: String) = cartDao.clearCart(userId)
}