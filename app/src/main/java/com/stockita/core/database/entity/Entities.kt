package com.stockita.core.database.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "categories")
data class CategoryEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val type: String // "PRODUK" or "BAHAN"
)

@Entity(tableName = "products")
data class ProductEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val categoryId: Long?,
    val price: Long,
    val stockMode: String, // "NONE", "DIRECT", "RECIPE"
    val stock: Double,
    val isActive: Boolean = true
)

@Entity(tableName = "materials")
data class MaterialEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val unit: String,
    val stock: Double,
    val minStock: Double,
    val lastCost: Long,
    val categoryId: Long?
)

@Entity(
    tableName = "recipe_items",
    indices = [Index("productId"), Index("materialId")]
)
data class RecipeItemEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val productId: Long,
    val materialId: Long,
    val qty: Double
)

@Entity(
    tableName = "transactions",
    indices = [Index("createdAt")]
)
data class TransactionEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val createdAt: Long = System.currentTimeMillis(),
    val subtotal: Long,
    val discount: Long,
    val total: Long,
    val paymentMethod: String,
    val note: String? = null
)

@Entity(
    tableName = "transaction_items",
    indices = [Index("transactionId"), Index("productId")]
)
data class TransactionItemEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val transactionId: Long,
    val productId: Long,
    val nameSnapshot: String,
    val priceSnapshot: Long,
    val hppSnapshot: Long,
    val qty: Double
)

@Entity(
    tableName = "stock_movements",
    indices = [Index("targetType", "targetId"), Index("createdAt")]
)
data class StockMovementEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val targetType: String, // "PRODUCT" or "MATERIAL"
    val targetId: Long,
    val qty: Double, // +/-
    val reason: String, // "PURCHASE", "SALE", "ADJUST", "WASTE"
    val refId: Long?, // Nullable, can refer to transactionId, etc.
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(
    tableName = "expenses",
    indices = [Index("createdAt")]
)
data class ExpenseEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val amount: Long,
    val category: String,
    val createdAt: Long = System.currentTimeMillis(),
    val note: String? = null
)

@Entity(
    tableName = "tasks",
    indices = [Index("isDone", "dueAt"), Index("status")]
)
data class TaskEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val note: String? = null,
    val dueAt: Long? = null,
    val isDone: Boolean = false,
    val priority: Int = 1, // 0: Rendah, 1: Sedang, 2: Tinggi
    val status: String = "TODO", // "TODO", "IN_PROGRESS", "DONE"
    val refType: String? = null, // "MATERIAL" | "PRODUCT" | "EXPENSE" | null
    val refId: Long? = null,
    val createdAt: Long = System.currentTimeMillis()
)
