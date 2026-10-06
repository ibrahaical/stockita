package com.stockita.core.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.stockita.core.database.dao.*
import com.stockita.core.database.entity.*

@Database(
    entities = [
        CategoryEntity::class,
        ProductEntity::class,
        MaterialEntity::class,
        RecipeItemEntity::class,
        TransactionEntity::class,
        TransactionItemEntity::class,
        StockMovementEntity::class,
        ExpenseEntity::class,
        TaskEntity::class
    ],
    version = 3,
    exportSchema = false
)
abstract class StockitaDatabase : RoomDatabase() {
    abstract fun productDao(): ProductDao
    abstract fun materialDao(): MaterialDao
    abstract fun transactionDao(): TransactionDao
    abstract fun taskDao(): TaskDao
    abstract fun checkoutDao(): CheckoutDao
    abstract fun stockDao(): StockDao
    abstract fun recipeDao(): com.stockita.core.database.dao.RecipeDao
    abstract fun expenseDao(): ExpenseDao
    abstract fun reportDao(): com.stockita.core.database.dao.ReportDao
}
