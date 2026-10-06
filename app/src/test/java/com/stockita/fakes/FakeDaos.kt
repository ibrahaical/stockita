package com.stockita.fakes

import com.stockita.core.database.dao.*
import com.stockita.core.database.entity.*
import com.stockita.core.database.model.LabaRugiResult
import com.stockita.core.database.model.RecipeItemWithMaterial
import com.stockita.core.database.model.TopProductResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map

class FakeProductDao : ProductDao {
    private val products = MutableStateFlow<List<ProductEntity>>(emptyList())
    private var nextId = 1L

    override fun getAllProducts(): Flow<List<ProductEntity>> = products.asStateFlow()

    override suspend fun insertProduct(product: ProductEntity): Long {
        val id = if (product.id == 0L) nextId++ else product.id
        val updated = products.value.filter { it.id != id } + product.copy(id = id)
        products.value = updated.sortedBy { it.name }
        return id
    }

    override suspend fun updateProduct(product: ProductEntity) {
        val updated = products.value.map { if (it.id == product.id) product else it }
        products.value = updated.sortedBy { it.name }
    }
}

class FakeMaterialDao : MaterialDao {
    private val materials = MutableStateFlow<List<MaterialEntity>>(emptyList())
    private var nextId = 1L

    override fun getAllMaterials(): Flow<List<MaterialEntity>> = materials.asStateFlow()

    override suspend fun insertMaterial(material: MaterialEntity): Long {
        val id = if (material.id == 0L) nextId++ else material.id
        val updated = materials.value.filter { it.id != id } + material.copy(id = id)
        materials.value = updated.sortedBy { it.name }
        return id
    }

    override suspend fun updateMaterial(material: MaterialEntity) {
        val updated = materials.value.map { if (it.id == material.id) material else it }
        materials.value = updated.sortedBy { it.name }
    }
}

class FakeRecipeDao : RecipeDao {
    private val recipeItems = MutableStateFlow<List<RecipeItemEntity>>(emptyList())
    var materialsProvider: () -> List<MaterialEntity> = { emptyList() }
    private var nextId = 1L

    override fun getRecipeForProduct(productId: Long): Flow<List<RecipeItemWithMaterial>> {
        return recipeItems.map { list ->
            val materialsMap = materialsProvider().associateBy { it.id }
            list.filter { it.productId == productId }.mapNotNull { rItem ->
                val mat = materialsMap[rItem.materialId]
                if (mat != null) {
                    RecipeItemWithMaterial(
                        recipeItemId = rItem.id,
                        productId = rItem.productId,
                        materialId = rItem.materialId,
                        qty = rItem.qty,
                        materialName = mat.name,
                        unit = mat.unit,
                        lastCost = mat.lastCost
                    )
                } else null
            }
        }
    }

    override suspend fun insertRecipeItem(item: RecipeItemEntity): Long {
        val id = if (item.id == 0L) nextId++ else item.id
        val updated = recipeItems.value.filter { it.id != id } + item.copy(id = id)
        recipeItems.value = updated
        return id
    }

    override suspend fun deleteRecipeItem(item: RecipeItemEntity) {
        recipeItems.value = recipeItems.value.filter { it.id != item.id }
    }
}

class FakeTransactionDao : TransactionDao {
    val transactions = MutableStateFlow<List<TransactionEntity>>(emptyList())
    val transactionItems = MutableStateFlow<List<TransactionItemEntity>>(emptyList())

    override fun getAllTransactions(): Flow<List<TransactionEntity>> = transactions.asStateFlow()
    override fun getAllTransactionItems(): Flow<List<TransactionItemEntity>> = transactionItems.asStateFlow()
}

class FakeTaskDao : TaskDao {
    val tasks = MutableStateFlow<List<TaskEntity>>(emptyList())
    private var nextId = 1L

    override fun getAllTasks(): Flow<List<TaskEntity>> = tasks.asStateFlow()

    override suspend fun insertTask(task: TaskEntity): Long {
        val id = if (task.id == 0L) nextId++ else task.id
        val updated = tasks.value.filter { it.id != id } + task.copy(id = id)
        tasks.value = updated.sortedWith(compareBy({ it.isDone }, { it.dueAt ?: Long.MAX_VALUE }))
        return id
    }

    override suspend fun updateTask(task: TaskEntity) {
        val updated = tasks.value.map { if (it.id == task.id) task else it }
        tasks.value = updated.sortedWith(compareBy({ it.isDone }, { it.dueAt ?: Long.MAX_VALUE }))
    }
}

class FakeExpenseDao : ExpenseDao {
    val expenses = MutableStateFlow<List<ExpenseEntity>>(emptyList())
    private var nextId = 1L

    override fun getAllExpenses(): Flow<List<ExpenseEntity>> = expenses.asStateFlow()

    override suspend fun insertExpense(expense: ExpenseEntity): Long {
        val id = if (expense.id == 0L) nextId++ else expense.id
        val updated = expenses.value.filter { it.id != id } + expense.copy(id = id)
        expenses.value = updated.sortedByDescending { it.createdAt }
        return id
    }

    override suspend fun deleteExpense(expense: ExpenseEntity) {
        expenses.value = expenses.value.filter { it.id != expense.id }
    }
}

class FakeCheckoutDao : CheckoutDao() {
    var lastInsertedTransaction: TransactionEntity? = null
    val insertedTransactions = mutableListOf<TransactionEntity>()
    val insertedItems = mutableListOf<TransactionItemEntity>()
    val insertedMovements = mutableListOf<StockMovementEntity>()
    
    val productStocks = mutableMapOf<Long, Double>()
    val materialStocks = mutableMapOf<Long, Double>()
    
    private var nextTransactionId = 1L

    override suspend fun insertTransaction(trx: TransactionEntity): Long {
        val id = nextTransactionId++
        val saved = trx.copy(id = id)
        lastInsertedTransaction = saved
        insertedTransactions.add(saved)
        return id
    }

    override suspend fun insertItems(items: List<TransactionItemEntity>) {
        insertedItems.addAll(items)
    }

    override suspend fun insertMovements(movements: List<StockMovementEntity>) {
        insertedMovements.addAll(movements)
    }

    override suspend fun adjustProductStock(productId: Long, qty: Double) {
        val current = productStocks.getOrDefault(productId, 0.0)
        productStocks[productId] = current + qty
    }

    override suspend fun adjustMaterialStock(materialId: Long, qty: Double) {
        val current = materialStocks.getOrDefault(materialId, 0.0)
        materialStocks[materialId] = current + qty
    }
}

class FakeStockDao : StockDao() {
    val productStocks = mutableMapOf<Long, Double>()
    val materialStocks = mutableMapOf<Long, Double>()
    val movements = MutableStateFlow<List<StockMovementEntity>>(emptyList())
    private var nextMovementId = 1L

    override suspend fun updateMaterialStock(materialId: Long, qty: Double) {
        val current = materialStocks.getOrDefault(materialId, 0.0)
        materialStocks[materialId] = current + qty
    }

    override suspend fun updateProductStock(productId: Long, qty: Double) {
        val current = productStocks.getOrDefault(productId, 0.0)
        productStocks[productId] = current + qty
    }

    override suspend fun insertStockMovement(movement: StockMovementEntity) {
        val id = if (movement.id == 0L) nextMovementId++ else movement.id
        movements.value = listOf(movement.copy(id = id)) + movements.value
    }

    override fun getAllMovements(): Flow<List<StockMovementEntity>> = movements.asStateFlow()
}

class FakeReportDao(
    private val transactions: () -> List<TransactionEntity>,
    private val transactionItems: () -> List<TransactionItemEntity>,
    private val expenses: () -> List<ExpenseEntity>,
    private val products: () -> List<ProductEntity>
) : ReportDao {

    override fun getLabaRugiSummary(startDate: Long, endDate: Long): Flow<LabaRugiResult> {
        return MutableStateFlow(
            calculateLabaRugi(startDate, endDate)
        )
    }

    private fun calculateLabaRugi(startDate: Long, endDate: Long): LabaRugiResult {
        val validTrx = transactions().filter { it.createdAt in startDate..endDate }
        val omzet = validTrx.sumOf { it.total }

        val validTrxIds = validTrx.map { it.id }.toSet()
        val hpp = transactionItems()
            .filter { it.transactionId in validTrxIds }
            .sumOf { (it.hppSnapshot * it.qty).toLong() }

        val expenseSum = expenses()
            .filter { it.createdAt in startDate..endDate }
            .sumOf { it.amount }

        return LabaRugiResult(omzet = omzet, hpp = hpp, expenses = expenseSum)
    }

    override fun getTopProducts(startDate: Long, endDate: Long): Flow<List<TopProductResult>> {
        val validTrx = transactions().filter { it.createdAt in startDate..endDate }
        val validTrxIds = validTrx.map { it.id }.toSet()
        val items = transactionItems().filter { it.transactionId in validTrxIds }
        val productMap = products().associateBy { it.id }

        val grouped = items.groupBy { it.productId }
        val results = grouped.map { (productId, pItems) ->
            val name = productMap[productId]?.name ?: (pItems.firstOrNull()?.nameSnapshot ?: "Produk")
            val totalQty = pItems.sumOf { it.qty }
            val totalRevenue = pItems.sumOf { (it.qty * it.priceSnapshot).toLong() }
            TopProductResult(
                itemName = name,
                totalQty = totalQty,
                totalRevenue = totalRevenue
            )
        }.sortedByDescending { it.totalQty }

        return MutableStateFlow(results)
    }
}
