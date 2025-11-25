package com.example.assignmentexample.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.assignmentexample.R
import com.example.assignmentexample.data.Address.AddressDao
import com.example.assignmentexample.data.Address.AddressEntity
import com.example.assignmentexample.data.Cart.CartDao
import com.example.assignmentexample.data.Cart.CartItemEntity
import com.example.assignmentexample.data.Drink.DrinkDao
import com.example.assignmentexample.data.Drink.DrinkEntity
import com.example.assignmentexample.data.History.OrderHistoryDao
import com.example.assignmentexample.data.History.OrderHistoryEntity
import com.example.assignmentexample.data.CreditCard.CreditCardDao
import com.example.assignmentexample.data.CreditCard.CreditCardEntity
import com.example.assignmentexample.data.Reward.RewardTransaction
import com.example.assignmentexample.data.Reward.RewardTransactionDao
import com.example.assignmentexample.data.UserPoints.UserPoints
import com.example.assignmentexample.data.UserPoints.UserPointsDao
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities =
        [
            CartItemEntity::class,
            OrderHistoryEntity::class,
            UserPoints::class,
            RewardTransaction::class,
            DrinkEntity::class,
            CreditCardEntity::class,
            AddressEntity::class
        ],
    version = 16,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun cartDao(): CartDao
    abstract fun orderHistoryDao(): OrderHistoryDao
    abstract fun userPointsDao(): UserPointsDao
    abstract fun rewardTransactionDao(): RewardTransactionDao
    abstract fun drinkDao(): DrinkDao
    abstract fun creditCardDao(): CreditCardDao
    abstract fun addressDao(): AddressDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "app_database"
                )
                    .addCallback(DatabaseCallback(context))
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }

    //Pre-populates the database with drinks
    private class DatabaseCallback(private val context: Context) : RoomDatabase.Callback() {
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            INSTANCE?.let { database ->
                CoroutineScope(Dispatchers.IO).launch {
                    populateDatabase(database.drinkDao())
                }
            }
        }

        suspend fun populateDatabase(drinkDao: DrinkDao) {
            val sundaeList = listOf(
                DrinkEntity(name = "Strawberry Sundae", chinese = "草莓聖代", price = 6.00, imageRes = R.drawable.strawberry, category = "Sundae", pointsValue = 60),
                DrinkEntity(name = "Chocolate Sundae", chinese = "巧克力聖代", price = 7.00, imageRes = R.drawable.chocolate, category = "Sundae", pointsValue = 70),
                DrinkEntity(name = "Brown Sugar Sundae", chinese = "黑糖珍珠聖代", price = 8.00, imageRes = R.drawable.brownsugar, category = "Sundae", pointsValue = 80),
                DrinkEntity(name = "Oreo Sundae", chinese = "奧利奧聖代", price = 6.00, imageRes = R.drawable.oreo, category = "Sundae", pointsValue = 60),
                DrinkEntity(name = "Banana Sundae", chinese = "香蕉聖代", price = 7.00, imageRes = R.drawable.banana, category = "Sundae", pointsValue = 70),
                DrinkEntity(name = "Coconut Sundae", chinese = "椰果聖代", price = 8.00, imageRes = R.drawable.coconut, category = "Sundae", pointsValue = 80),
                DrinkEntity(name = "Dragonfruit Sundae", chinese = "火龙果聖代", price = 6.00, imageRes = R.drawable.dragonfruit, category = "Sundae", pointsValue = 60),
                DrinkEntity(name = "Durian Sundae", chinese = "榴莲聖代", price = 8.00, imageRes = R.drawable.durian, category = "Sundae", pointsValue = 80),
            )

            val bubbleTeaList = listOf(
                DrinkEntity(name = "Classic Milk Tea", chinese = "经典奶茶", price = 6.50, imageRes=R.drawable.milktea, category = "Bubble Tea", pointsValue = 65),
                DrinkEntity(name = "Brown Sugar Boba Milk", chinese = "黑糖珍珠奶", price = 7.50, imageRes=R.drawable.blackmilktea, category = "Bubble Tea", pointsValue = 75),
                DrinkEntity(name = "Matcha Latte", chinese = "抹茶拿铁", price = 8.00, imageRes=R.drawable.matcha_latte, category = "Bubble Tea", pointsValue = 80),
                DrinkEntity(name = "Taro Milk Tea", chinese = "芋头奶茶", price = 7.00, imageRes = R.drawable.taro, category = "Bubble Tea", pointsValue = 70),
                DrinkEntity(name = "Oolong Milk Tea", chinese = "乌龙奶茶", price = 7.00, imageRes = R.drawable.oolong, category = "Bubble Tea", pointsValue = 70)
            )

            val juiceList = listOf(
                DrinkEntity(name = "Orange Juice", chinese = "橙汁", price = 5.00, imageRes = R.drawable.orange, category = "Juice", pointsValue = 50),
                DrinkEntity(name = "Apple Juice", chinese = "苹果汁", price = 5.50, imageRes = R.drawable.applejuice, category = "Juice", pointsValue = 55),
                DrinkEntity(name = "Watermelon Juice", chinese = "西瓜汁", price = 6.00, imageRes = R.drawable.watermelon, category = "Juice", pointsValue = 60),
                DrinkEntity(name = "Pineapple Juice", chinese = "菠萝汁", price = 6.00, imageRes = R.drawable.pineapple, category = "Juice", pointsValue = 60),
                DrinkEntity(name = "Mango Juice", chinese = "芒果汁", price = 6.50, imageRes = R.drawable.mangojuice, category = "Juice", pointsValue = 65)
            )

            drinkDao.insertAll(sundaeList+bubbleTeaList+juiceList)
        }
    }
}
