package com.example.assignmentexample.navigation

import android.app.Application
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.assignmentexample.data.*
import com.example.assignmentexample.data.Address.AddressViewModel
import com.example.assignmentexample.data.Address.AddressViewModelFactory
import com.example.assignmentexample.data.Cart.CartItemEntity
import com.example.assignmentexample.data.Cart.CartRepository
import com.example.assignmentexample.data.Cart.CartViewModel
import com.example.assignmentexample.data.Cart.CartViewModelFactory
import com.example.assignmentexample.data.CreditCard.CreditCardViewModel
import com.example.assignmentexample.data.CreditCard.CreditCardViewModelFactory
import com.example.assignmentexample.data.Drink.DrinkDao
import com.example.assignmentexample.data.Drink.DrinkDetailViewModel
import com.example.assignmentexample.data.Drink.DrinkDetailViewModelFactory
import com.example.assignmentexample.data.Drink.DrinkMenuViewModel
import com.example.assignmentexample.data.Drink.DrinkMenuViewModelFactory
import com.example.assignmentexample.data.History.OrderHistoryEntity
import com.example.assignmentexample.data.History.OrderHistoryViewModel
import com.example.assignmentexample.data.History.OrderHistoryViewModelFactory
import com.example.assignmentexample.data.Promotion.PromotionViewModel
import com.example.assignmentexample.data.Promotion.PromotionViewModelFactory
import com.example.assignmentexample.data.Reward.RewardTransaction
import com.example.assignmentexample.data.UserAccount.UserRepository
import com.example.assignmentexample.data.UserPoints.UserPoints
import com.example.assignmentexample.screen.*
import com.example.assignmentexample.screen.SignupScreen.CreateUsernameScreen

import com.example.assignmentexample.screens.account.AccountScreen
import com.example.assignmentexample.screens.auth.BingXueWelcomeScreen
import com.example.assignmentexample.screens.auth.LoginScreen
import com.example.assignmentexample.screens.main.HomeScreen
import com.example.assignmentexample.screens.promotion.PromotionScreen
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

const val LOYALTY_CARD_BONUS_POINTS = 100

suspend fun sendOrderEmail(context: Context, recipientEmail: String, cartItems: List<CartItemEntity>, drinkDao: DrinkDao) {
    val emailBody = StringBuilder()
    emailBody.append("New Order Details:\n\n")
    var totalAmount = 0.0

    withContext(Dispatchers.IO) {
        cartItems.forEach { item ->
            val drink = drinkDao.getDrinkById(item.drinkId) //look up drink by ID
            if (drink != null) {
                emailBody.append("• ${drink.name} (x${item.quantity})\n")
                emailBody.append("  - Details: ${item.sweetness}, ${item.size}, ${item.ice}\n")
                emailBody.append("  - Price: RM ${"%.2f".format(item.totalPrice)}\n\n")
                totalAmount += item.totalPrice
            }
        }
    }
    emailBody.append("--------------------\n")
    emailBody.append("Total Amount: RM ${"%.2f".format(totalAmount)}")

    val intent = Intent(Intent.ACTION_SENDTO).apply {
        data = Uri.parse("mailto:")
        putExtra(Intent.EXTRA_EMAIL, arrayOf(recipientEmail))
        putExtra(Intent.EXTRA_SUBJECT, "New Order Received from BINGXUE App")
        putExtra(Intent.EXTRA_TEXT, emailBody.toString())
        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    }
    context.startActivity(intent)
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun AppNavigation(application: Application) {
    val navController = rememberNavController()
    val scope = rememberCoroutineScope()
    val db = AppDatabase.getDatabase(application)
    val userRepository = remember { UserRepository() }
    val userViewModel: UserViewModel = viewModel(factory = UserViewModelFactory(userRepository))

    val creditCardViewModel: CreditCardViewModel = viewModel(factory = CreditCardViewModelFactory(application, userViewModel))
    val addressViewModel: AddressViewModel = viewModel(factory = AddressViewModelFactory(application, userViewModel))
    val cartRepository = remember { CartRepository(db.cartDao()) }
    val cartViewModel: CartViewModel = viewModel(factory = CartViewModelFactory(cartRepository, application, userViewModel))
    val promotionViewModel: PromotionViewModel = viewModel(factory = PromotionViewModelFactory(application, userViewModel))
    val orderHistoryViewModel: OrderHistoryViewModel = viewModel(factory = OrderHistoryViewModelFactory(application, userViewModel))
    val drinkMenuViewModel: DrinkMenuViewModel = viewModel(factory = DrinkMenuViewModelFactory(application))

    NavHost(navController = navController, startDestination = "welcome") {
        //Authentication
        composable("welcome") {
            BingXueWelcomeScreen(
                onLoginClick = { navController.navigate("login") },
                onSignUpClick = { navController.navigate("signUp") })
        }
        composable("login") {
            LoginScreen(
                userViewModel = userViewModel,
                onLoginSuccess = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo("welcome") {
                            inclusive = true
                        }
                    }
                },
                onNavigateBack = { navController.popBackStack() },
                onSignUpClick = { navController.navigate("signUp") },
                onForgotPasswordClick = { navController.navigate("forgot_password") })
        }
        composable("signUp") {
            SignUpScreen(
                userViewModel = userViewModel,
                onSignUpSuccess = { email -> navController.navigate("createUsername/$email") },
                onNavigateBack = { navController.popBackStack() })
        }
        composable(
            route = "createUsername/{email}",
            arguments = listOf(navArgument("email") { type = NavType.StringType })
        ) { backStackEntry ->
            val email = backStackEntry.arguments?.getString("email") ?: ""
            CreateUsernameScreen(
                userViewModel = userViewModel,
                email = email,
                onCreationSuccess = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo("welcome") {
                            inclusive = true
                        }
                    }
                })
        }
        composable("forgot_password") {
            ForgotPasswordScreen(
                userViewModel = userViewModel,
                onLinkSent = { navController.popBackStack() },
                onNavigateBack = { navController.popBackStack() })
        }

        //main app flow
        composable(Screen.Home.route) {
            HomeScreen(
                navController = navController,
                userViewModel = userViewModel,
                promotionViewModel = promotionViewModel
            )
        }
        composable(Screen.Menu.route) {
            DrinkMenuScreen(
                navController = navController,
                userViewModel = userViewModel,
                drinkMenuViewModel = drinkMenuViewModel
            )
        }
        composable(Screen.Promotion.route) {
            PromotionScreen(
                navController = navController,
                userViewModel = userViewModel,
                promotionViewModel = promotionViewModel
            )
        }
        composable(Screen.Account.route) {
            AccountScreen(
                navController = navController,
                userViewModel = userViewModel,
                promotionViewModel = promotionViewModel,
                orderHistoryViewModel = orderHistoryViewModel
            )
        }

        //profile&others
        composable("edit_profile") {
            EditProfileScreen(
                navController = navController,
                userViewModel = userViewModel
            )
        }

        composable("address_screen") { AddressScreen(navController, addressViewModel) }
        composable("add_address") { AddAddressScreen(navController, addressViewModel) }

        composable("faq") { FaqScreen(navController = navController) }
        composable("order_history") {
            OrderHistoryScreen(
                navController = navController,
                orderHistoryViewModel = orderHistoryViewModel
            )
        }

        composable("payment_methods") {
            PaymentMethodScreen(navController = navController, paymentViewModel = creditCardViewModel)
        }
        composable("add_credit_card") {
            AddCreditCardScreen(navController = navController, paymentViewModel = creditCardViewModel)
        }

        //redeem
        composable("redeem_menu") {
            RedeemDrinkMenuScreen(
                navController = navController,
                drinkMenuViewModel = drinkMenuViewModel,
                promotionViewModel = promotionViewModel
            )
        }
        composable(
            "redeem_detail/{drinkId}",
            arguments = listOf(navArgument("drinkId") { type = NavType.IntType })
        ) { backStackEntry ->
            val drinkId = backStackEntry.arguments?.getInt("drinkId")
            if (drinkId != null) {
                val drinkDetailViewModel: DrinkDetailViewModel =
                    viewModel(factory = DrinkDetailViewModelFactory(application, drinkId))
                RedeemDrinkDetailScreen(
                    navController = navController,
                    drinkDetailViewModel = drinkDetailViewModel,
                    promotionViewModel = promotionViewModel
                )
            }
        }
        composable("redeem_success") { RedemptionSuccessScreen(navController = navController) }

        //drink order flow
        composable(
            route = "detail/{drinkId}",
            arguments = listOf(navArgument("drinkId") { type = NavType.IntType })
        ) { backStackEntry ->
            val drinkId = backStackEntry.arguments?.getInt("drinkId")
            if (drinkId != null) {
                val drinkDetailViewModel: DrinkDetailViewModel =
                    viewModel(factory = DrinkDetailViewModelFactory(application, drinkId))
                DrinkDetailScreen(
                    navController = navController,
                    drinkDetailViewModel = drinkDetailViewModel,
                    cartViewModel = cartViewModel
                )
            }
        }

        composable("cart") {
            CartScreen(navController = navController, cartViewModel = cartViewModel)
        }

        composable(
            route = "confirmation/{totalAmount}",
            arguments = listOf(navArgument("totalAmount") { type = NavType.StringType })
        ) { backStackEntry ->
            OrderConfirmationScreen(
                navController = navController,
                totalAmount = backStackEntry.arguments?.getString("totalAmount"),
                userViewModel = userViewModel,
                paymentViewModel = creditCardViewModel,
                addressViewModel = addressViewModel
            )
        }


        //payment
        composable("payment_successful") {
            PaymentSuccessfulScreen(
                navController = navController,
                onDone = {
                    val cartItemsToSave = cartViewModel.cartDisplayItems.value
                    if (cartItemsToSave.isNotEmpty()) {
                        scope.launch(Dispatchers.IO) {
                            val cartItemsToSave = cartViewModel.cartDisplayItems.value
                            if (cartItemsToSave.isEmpty()) return@launch

                            val currentUserId = userViewModel.userProfile.value?.uid
                            if (currentUserId == null) {
                                withContext(Dispatchers.Main) {
                                    Toast.makeText(application, "Error: User not logged in.", Toast.LENGTH_LONG).show()
                                }
                                return@launch
                            }

                            val userPointsDao = db.userPointsDao()
                            val orderHistoryDao = db.orderHistoryDao()
                            val rewardTransactionDao = db.rewardTransactionDao()
                            val drinkDao = db.drinkDao()
                            val settingsManager = SettingsManager(application)

                            //determine final method
                            val finalPaymentMethod = when (settingsManager.defaultPaymentType) {
                                "Credit Card" -> {
                                    val card = db.creditCardDao().getCardById(settingsManager.defaultCardId)
                                    if (card != null) "Credit Card (...${card.cardNumberLast4})" else "Credit Card"
                                }
                                "Online Banking" -> "Online Banking (${settingsManager.savedBank})"
                                "Cash" -> "Cash on Delivery"
                                else -> "Unknown"
                            }

                            //generate order id and timestamp
                            val newOrderIdNumber = settingsManager.lastOrderId + 1
                            settingsManager.lastOrderId = newOrderIdNumber
                            val formattedOrderId = "ORD$newOrderIdNumber"
                            val orderTimestamp = System.currentTimeMillis()

                            //save each item with the new paymentMethod field
                            for (displayItem in cartItemsToSave) {
                                val orderHistoryItem = OrderHistoryEntity(
                                    userOwnerId = currentUserId,
                                    orderId = formattedOrderId,
                                    drinkId = displayItem.drink.drinkId,
                                    details = "${displayItem.cartItem.size} | ${displayItem.cartItem.sweetness} | ${displayItem.cartItem.ice}",
                                    quantity = displayItem.cartItem.quantity,
                                    price = displayItem.cartItem.totalPrice,
                                    orderDate = orderTimestamp,
                                    paymentMethod = finalPaymentMethod
                                )
                                orderHistoryDao.insertOrder(orderHistoryItem)
                            }

                            var pointsFromPurchase = 0
                            cartItemsToSave.forEach { pointsFromPurchase += it.drink.pointsValue * it.cartItem.quantity }

                            //fetch points for that specific user
                            val currentUserPoints = userPointsDao.getUserPoints(currentUserId).first() ?: UserPoints(userId = currentUserId, points = 0, loyaltyStamps = 0)

                            val previousStamps = currentUserPoints.loyaltyStamps
                            val stampsToAdd = cartItemsToSave.size
                            val totalStampsBeforeReset = previousStamps + stampsToAdd
                            var pointsFromBonus = 0

                            if (totalStampsBeforeReset >= 8) {
                                pointsFromBonus = LOYALTY_CARD_BONUS_POINTS
                                rewardTransactionDao.insertTransaction(
                                    RewardTransaction(
                                        userOwnerId = currentUserId, // <-- TAG DATA
                                        rewardName = "Loyalty Card Bonus",
                                        pointsChange = pointsFromBonus
                                    )
                                )
                            }

                            val newTotalPoints = currentUserPoints.points + pointsFromPurchase + pointsFromBonus
                            val newStamps = totalStampsBeforeReset.rem(8)

                            //update points for that specific user
                            userPointsDao.upsertUser(currentUserPoints.copy(points = newTotalPoints, loyaltyStamps = newStamps))

                            if (pointsFromPurchase > 0) {
                                rewardTransactionDao.insertTransaction(
                                    RewardTransaction(
                                        userOwnerId = currentUserId, // <-- TAG DATA
                                        rewardName = "Order Purchase",
                                        pointsChange = pointsFromPurchase
                                    )
                                )
                            }

                            val rawCartEntities = cartItemsToSave.map { it.cartItem }

                            withContext(Dispatchers.Main) {
                                cartViewModel.clearCart()
                                Toast.makeText(
                                    application,
                                    "Order placed successfully!",
                                    Toast.LENGTH_SHORT
                                ).show()
                                sendOrderEmail(
                                    application,
                                    "ngmw-wm24@student.tarc.edu.my",
                                    rawCartEntities,
                                    drinkDao
                                )
                                navController.navigate(Screen.Home.route) {
                                    popUpTo(Screen.Home.route) { inclusive = true }
                                }
                            }
                        }
                    }
                }
            )
        }
    }
}