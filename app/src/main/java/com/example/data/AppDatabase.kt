package com.example.data

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [
        User::class,
        Category::class,
        Supplier::class,
        Product::class,
        StockLog::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun shopDao(): ShopDao
}
