package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.data.Category
import com.example.data.Product
import com.example.data.User

@Composable
fun InventoryScreen(
    user: User,
    products: List<Product>,
    categories: List<Category>,
    searchQuery: String,
    onSearchChange: (String) -> Unit,
    onAddProduct: () -> Unit,
    onEditProduct: (Product) -> Unit,
    onDeleteProduct: (Product) -> Unit,
    onStockChange: (Product, Int) -> Unit
) {
    Scaffold(
        floatingActionButton = {
            if (user.role == "Admin") {
                FloatingActionButton(onClick = onAddProduct) {
                    Icon(Icons.Default.Add, contentDescription = "Add Product")
                }
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = onSearchChange,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                placeholder = { Text("Search by name or SKU...") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                singleLine = true
            )

            if (products.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("No products found.", color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            } else {
                LazyColumn(
                    contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(products, key = { it.id }) { product ->
                        val category = categories.find { it.id == product.categoryId }
                        ProductCard(
                            product = product,
                            category = category,
                            user = user,
                            onEdit = { onEditProduct(product) },
                            onDelete = { onDeleteProduct(product) },
                            onStockChange = { amount -> onStockChange(product, amount) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ProductCard(
    product: Product,
    category: Category?,
    user: User,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onStockChange: (Int) -> Unit
) {
    val isLowStock = product.stockLevel < 5
    val badgeColor = if (isLowStock) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary
    val badgeText = if (product.stockLevel == 0) "Out of Stock" else if (isLowStock) "Low Stock" else "In Stock"

    Card(
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    product.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .background(badgeColor)
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(
                        badgeText,
                        color = MaterialTheme.colorScheme.onError,
                        style = MaterialTheme.typography.labelSmall
                    )
                }
            }

            Text("SKU: ${product.sku} | Category: ${category?.name ?: "None"}", style = MaterialTheme.typography.bodySmall)
            Text("Stock: ${product.stockLevel}  |  Price: $${product.unitPrice}", style = MaterialTheme.typography.bodyMedium)

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Quick Stock Adjustment
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = { onStockChange(-1) }, enabled = product.stockLevel > 0) {
                        Icon(Icons.Default.Remove, contentDescription = "Decrement", tint = MaterialTheme.colorScheme.error)
                    }
                    Text(product.stockLevel.toString(), fontWeight = FontWeight.Bold)
                    IconButton(onClick = { onStockChange(1) }) {
                        Icon(Icons.Default.Add, contentDescription = "Increment", tint = MaterialTheme.colorScheme.primary)
                    }
                }

                if (user.role == "Admin") {
                    Row {
                        IconButton(onClick = onEdit) {
                            Icon(Icons.Default.Edit, contentDescription = "Edit Product")
                        }
                        IconButton(onClick = onDelete) {
                            Icon(Icons.Default.Delete, contentDescription = "Delete Product")
                        }
                    }
                }
            }
        }
    }
}
