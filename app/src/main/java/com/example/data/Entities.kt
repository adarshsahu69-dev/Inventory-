package com.example.data

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class User(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val username: String,
    val role: String // "Admin" or "Staff"
)

@Entity(tableName = "categories")
data class Category(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val color: String = "#94a3b8" // Slate by default
)

@Entity(tableName = "suppliers")
data class Supplier(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val contactEmail: String,
    val phone: String
)

@Entity(
    tableName = "products",
    foreignKeys = [
        ForeignKey(entity = Category::class, parentColumns = ["id"], childColumns = ["categoryId"], onDelete = ForeignKey.SET_NULL),
        ForeignKey(entity = Supplier::class, parentColumns = ["id"], childColumns = ["supplierId"], onDelete = ForeignKey.SET_NULL)
    ]
)
data class Product(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val sku: String,
    val name: String,
    val description: String = "",
    val categoryId: Int?,
    val supplierId: Int?,
    val stockLevel: Int = 0,
    val unitPrice: Double = 0.0,
    val barcode: String = ""
)

@Entity(
    tableName = "stock_logs",
    foreignKeys = [
        ForeignKey(entity = Product::class, parentColumns = ["id"], childColumns = ["productId"], onDelete = ForeignKey.CASCADE),
        ForeignKey(entity = User::class, parentColumns = ["id"], childColumns = ["userId"], onDelete = ForeignKey.CASCADE)
    ]
)
data class StockLog(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val productId: Int,
    val userId: Int,
    val changeAmount: Int, // positive for addition, negative for removal
    val reason: String,
    val timestamp: Long = System.currentTimeMillis()
)
