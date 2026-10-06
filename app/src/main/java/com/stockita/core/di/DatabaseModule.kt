package com.stockita.core.di

import android.content.Context
import androidx.room.Room
import com.stockita.core.database.StockitaDatabase
import com.stockita.core.database.dao.CheckoutDao
import com.stockita.core.database.dao.MaterialDao
import com.stockita.core.database.dao.ProductDao
import com.stockita.core.database.dao.TaskDao
import com.stockita.core.database.dao.TransactionDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): StockitaDatabase {
        return Room.databaseBuilder(
            context,
            StockitaDatabase::class.java,
            "stockita.db"
        ).build()
    }

    @Provides
    fun provideProductDao(database: StockitaDatabase): ProductDao = database.productDao()

    @Provides
    fun provideMaterialDao(database: StockitaDatabase): MaterialDao = database.materialDao()

    @Provides
    fun provideTransactionDao(database: StockitaDatabase): TransactionDao = database.transactionDao()

    @Provides
    fun provideTaskDao(database: StockitaDatabase): TaskDao = database.taskDao()

    @Provides
    fun provideCheckoutDao(database: StockitaDatabase): CheckoutDao = database.checkoutDao()

    @Provides
    fun provideStockDao(database: StockitaDatabase): com.stockita.core.database.dao.StockDao = database.stockDao()

    @Provides
    fun provideRecipeDao(database: StockitaDatabase): com.stockita.core.database.dao.RecipeDao = database.recipeDao()

    @Provides
    fun provideExpenseDao(database: StockitaDatabase): com.stockita.core.database.dao.ExpenseDao = database.expenseDao()

    @Provides
    fun provideReportDao(database: StockitaDatabase): com.stockita.core.database.dao.ReportDao = database.reportDao()
}
