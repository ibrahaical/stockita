package com.stockita.core.database.model

data class LabaRugiResult(
    val omzet: Long?,
    val hpp: Long?,
    val expenses: Long?
)

data class TopProductResult(
    val itemName: String,
    val totalQty: Double,
    val totalRevenue: Long
)
