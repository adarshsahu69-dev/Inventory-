package com.example.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.room.Room
import com.example.data.AppDatabase
import com.example.data.Category
import com.example.data.Product
import com.example.data.ShopRepository
import com.example.data.StockLog
import com.example.data.Supplier
import com.example.data.User
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ShopViewModel(application: Application) : AndroidViewModel(application) {
    private val db = Room.databaseBuilder(
        application,
        AppDatabase::class.java,
        "shop-db"
    ).build()

    private val repository = ShopRepository(db.shopDao())

    val allProducts: StateFlow<List<Product>> = repository.allProducts
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allCategories: StateFlow<List<Category>> = repository.allCategories
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allSuppliers: StateFlow<List<Supplier>> = repository.allSuppliers
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val recentLogs: StateFlow<List<StockLog>> = repository.recentLogs
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _currentUser = MutableStateFlow<User?>(null)
    val currentUser = _currentUser.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery = _searchQuery.asStateFlow()

    private val _isDarkMode = MutableStateFlow<Boolean?>(null)
    val isDarkMode = _isDarkMode.asStateFlow()

    val filteredProducts: StateFlow<List<Product>> = combine(allProducts, _searchQuery) { products, query ->
        if (query.isBlank()) products else products.filter {
            it.name.contains(query, ignoreCase = true) || it.sku.contains(query, ignoreCase = true)
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Dashboard Metrics
    val lowStockProducts: StateFlow<List<Product>> = allProducts.map { products ->
        products.filter { it.stockLevel < 5 }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val totalInventoryValue: StateFlow<Double> = allProducts.map { products ->
        products.sumOf { it.stockLevel * it.unitPrice }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    init {
        viewModelScope.launch {
            repository.loadInitialDataIfEmpty()
        }
    }

    fun login(username: String, onResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            val user = repository.getUserByUsername(username)
            if (user != null) {
                _currentUser.value = user
                onResult(true)
            } else {
                onResult(false)
            }
        }
    }

    fun logout() {
        _currentUser.value = null
    }

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun toggleTheme(currentSystemDark: Boolean) {
        val current = _isDarkMode.value ?: currentSystemDark
        _isDarkMode.value = !current
    }

    fun updateStock(productId: Int, change: Int, reason: String) {
        val user = _currentUser.value ?: return
        viewModelScope.launch {
            repository.updateStock(productId, user.id, change, reason)
        }
    }

    fun saveProduct(product: Product) {
        viewModelScope.launch {
            repository.saveProduct(product)
        }
    }

    fun updateProduct(product: Product) {
        viewModelScope.launch {
            repository.updateProduct(product)
        }
    }

    fun deleteProduct(product: Product) {
        viewModelScope.launch {
            repository.deleteProduct(product)
        }
    }
}
