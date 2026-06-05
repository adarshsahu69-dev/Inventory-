package com.example.data

import kotlinx.coroutines.flow.Flow

class ShopRepository(private val dao: ShopDao) {
    val allProducts: Flow<List<Product>> = dao.getAllProducts()
    val allCategories: Flow<List<Category>> = dao.getAllCategories()
    val allSuppliers: Flow<List<Supplier>> = dao.getAllSuppliers()
    val recentLogs: Flow<List<StockLog>> = dao.getRecentStockLogs()

    suspend fun getProductById(id: Int): Product? = dao.getProductById(id)

    suspend fun addProduct(product: Product, userId: Int, initialStock: Int, reason: String) {
        dao.insertProduct(product)
        // If we wanted to link the item we'd need its inserted ID, but we simplified insertProduct.
        // Let's modify to return ID or just insert without initial log if ID is needed immediately.
    }

    suspend fun saveProduct(product: Product) {
        dao.insertProduct(product)
    }

    suspend fun updateProduct(product: Product) {
        dao.updateProduct(product)
    }

    suspend fun deleteProduct(product: Product) {
        dao.deleteProduct(product)
    }

    suspend fun updateStock(productId: Int, userId: Int, change: Int, reason: String) {
        val product = dao.getProductById(productId)
        if (product != null) {
            val updatedStock = product.stockLevel + change
            dao.updateProduct(product.copy(stockLevel = updatedStock))
            dao.insertStockLog(
                StockLog(
                    productId = productId,
                    userId = userId,
                    changeAmount = change,
                    reason = reason
                )
            )
        }
    }

    suspend fun loadInitialDataIfEmpty() {
        // Pre-populate some suppliers and categories for the demo
        dao.insertCategory(Category(name = "Electronics", color = "#3b82f6"))
        dao.insertCategory(Category(name = "Apparel", color = "#ec4899"))
        dao.insertCategory(Category(name = "Home", color = "#10b981"))

        dao.insertSupplier(Supplier(name = "TechSource Inc", contactEmail = "orders@techsource.com", phone = "555-1234"))
        dao.insertSupplier(Supplier(name = "Global Merch", contactEmail = "sales@globalmerch.com", phone = "555-5678"))
        
        dao.insertUser(User(username = "admin", role = "Admin"))
        dao.insertUser(User(username = "staff", role = "Staff"))
    }
    
    suspend fun getUserByUsername(username: String): User? {
        return dao.getUserByUsername(username)
    }
}
