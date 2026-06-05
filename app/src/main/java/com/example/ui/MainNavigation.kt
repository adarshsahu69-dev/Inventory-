package com.example.ui

import android.widget.Toast
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Inventory
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.People
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.ui.screens.AddEditProductScreen
import com.example.ui.screens.DashboardScreen
import com.example.ui.screens.InventoryScreen
import com.example.ui.screens.LoginScreen
import com.example.ui.screens.SuppliersScreen
import com.example.viewmodel.ShopViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainNavigation(viewModel: ShopViewModel) {
    val navController = rememberNavController()
    val currentUser by viewModel.currentUser.collectAsStateWithLifecycle()

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    
    val showBottomBar = currentRoute in listOf("dashboard", "inventory", "suppliers")
    val systemDark = isSystemInDarkTheme()

    LaunchedEffect(currentUser) {
        if (currentUser == null && currentRoute != "login") {
            navController.navigate("login") {
                popUpTo(0)
            }
        } else if (currentUser != null && currentRoute == "login") {
            navController.navigate("dashboard") {
                popUpTo("login") { inclusive = true }
            }
        }
    }

    Scaffold(
        topBar = {
            if (currentRoute != "login" && currentRoute != "add_product" && currentRoute?.startsWith("edit_product") != true) {
                TopAppBar(
                    title = { Text(
                        when (currentRoute) {
                            "dashboard" -> "Dashboard"
                            "inventory" -> "Inventory"
                            "suppliers" -> "Suppliers"
                            else -> "Shop Inventory"
                        }
                    ) },
                    actions = {
                        val isDarkMode = viewModel.isDarkMode.collectAsStateWithLifecycle().value ?: systemDark
                        IconButton(onClick = { viewModel.toggleTheme(systemDark) }) {
                            Icon(
                                imageVector = if (isDarkMode) Icons.Default.LightMode else Icons.Default.DarkMode,
                                contentDescription = "Toggle Theme"
                            )
                        }
                    }
                )
            }
        },
        bottomBar = {
            if (showBottomBar) {
                NavigationBar {
                    NavigationBarItem(
                        icon = { Icon(Icons.Default.Dashboard, contentDescription = "Dashboard") },
                        label = { Text("Dashboard") },
                        selected = currentRoute == "dashboard",
                        onClick = {
                            navController.navigate("dashboard") { popUpTo(navController.graph.startDestinationId) { saveState = true }; launchSingleTop = true; restoreState = true }
                        }
                    )
                    NavigationBarItem(
                        icon = { Icon(Icons.Default.Inventory, contentDescription = "Inventory") },
                        label = { Text("Inventory") },
                        selected = currentRoute == "inventory",
                        onClick = {
                            navController.navigate("inventory") { popUpTo(navController.graph.startDestinationId) { saveState = true }; launchSingleTop = true; restoreState = true }
                        }
                    )
                    NavigationBarItem(
                        icon = { Icon(Icons.Default.People, contentDescription = "Suppliers") },
                        label = { Text("Suppliers") },
                        selected = currentRoute == "suppliers",
                        onClick = {
                            navController.navigate("suppliers") { popUpTo(navController.graph.startDestinationId) { saveState = true }; launchSingleTop = true; restoreState = true }
                        }
                    )
                }
            }
        }
    ) { paddingValues ->
        NavHost(
            navController = navController,
            startDestination = "login",
            modifier = Modifier.padding(paddingValues)
        ) {
            composable("login") {
                val context = LocalContext.current
                LoginScreen(
                    onLogin = { username ->
                        viewModel.login(username) { success ->
                            if (!success) {
                                Toast.makeText(context, "Login failed", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                )
            }
            
            composable("dashboard") {
                val allProducts by viewModel.allProducts.collectAsStateWithLifecycle()
                val lowStock by viewModel.lowStockProducts.collectAsStateWithLifecycle()
                val totalValue by viewModel.totalInventoryValue.collectAsStateWithLifecycle()
                DashboardScreen(allProducts, lowStock, totalValue)
            }

            composable("inventory") {
                val context = LocalContext.current
                val products by viewModel.filteredProducts.collectAsStateWithLifecycle()
                val categories by viewModel.allCategories.collectAsStateWithLifecycle()
                val query by viewModel.searchQuery.collectAsStateWithLifecycle()
                
                currentUser?.let { user ->
                    InventoryScreen(
                        user = user,
                        products = products,
                        categories = categories,
                        searchQuery = query,
                        onSearchChange = viewModel::updateSearchQuery,
                        onAddProduct = { navController.navigate("add_product") },
                        onEditProduct = { navController.navigate("edit_product/${it.id}") },
                        onDeleteProduct = { product -> 
                            if (user.role == "Admin") {
                                viewModel.deleteProduct(product)
                                Toast.makeText(context, "Deleted ${product.name}", Toast.LENGTH_SHORT).show()
                            } else {
                                Toast.makeText(context, "Access Denied", Toast.LENGTH_SHORT).show()
                            }
                        },
                        onStockChange = { product, amount ->
                            viewModel.updateStock(product.id, amount, if (amount > 0) "Restock" else "Sale/Checkout")
                        }
                    )
                }
            }

            composable("suppliers") {
                val suppliers by viewModel.allSuppliers.collectAsStateWithLifecycle()
                val products by viewModel.allProducts.collectAsStateWithLifecycle()
                SuppliersScreen(suppliers, products)
            }

            composable("add_product") {
                val categories by viewModel.allCategories.collectAsStateWithLifecycle()
                val suppliers by viewModel.allSuppliers.collectAsStateWithLifecycle()
                val context = LocalContext.current
                
                AddEditProductScreen(
                    productToEdit = null,
                    categories = categories,
                    suppliers = suppliers,
                    onSave = { product -> 
                        viewModel.saveProduct(product)
                        Toast.makeText(context, "Product saved", Toast.LENGTH_SHORT).show()
                        navController.popBackStack()
                    },
                    onNavigateBack = { navController.popBackStack() }
                )
            }

            composable(
                "edit_product/{productId}",
                arguments = listOf(navArgument("productId") { type = NavType.IntType })
            ) { backStackEntry ->
                val productId = backStackEntry.arguments?.getInt("productId") ?: return@composable
                val allProducts by viewModel.allProducts.collectAsStateWithLifecycle()
                val productToEdit = allProducts.find { it.id == productId }
                
                val categories by viewModel.allCategories.collectAsStateWithLifecycle()
                val suppliers by viewModel.allSuppliers.collectAsStateWithLifecycle()
                val context = LocalContext.current
                
                AddEditProductScreen(
                    productToEdit = productToEdit,
                    categories = categories,
                    suppliers = suppliers,
                    onSave = { product -> 
                        viewModel.updateProduct(product)
                        Toast.makeText(context, "Product updated", Toast.LENGTH_SHORT).show()
                        navController.popBackStack()
                    },
                    onNavigateBack = { navController.popBackStack() }
                )
            }
        }
    }
}
