package com.example.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.data.Category
import com.example.data.Product
import com.example.data.Supplier

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditProductScreen(
    productToEdit: Product? = null,
    categories: List<Category>,
    suppliers: List<Supplier>,
    onSave: (Product) -> Unit,
    onNavigateBack: () -> Unit
) {
    var step by remember { mutableStateOf(1) }

    var name by remember { mutableStateOf(productToEdit?.name ?: "") }
    var sku by remember { mutableStateOf(productToEdit?.sku ?: "") }
    var description by remember { mutableStateOf(productToEdit?.description ?: "") }

    var unitPrice by remember { mutableStateOf(productToEdit?.unitPrice?.toString() ?: "") }
    var stockLevel by remember { mutableStateOf(productToEdit?.stockLevel?.toString() ?: "") }
    var barcode by remember { mutableStateOf(productToEdit?.barcode ?: "") }

    var categoryId by remember { mutableStateOf<Int?>(productToEdit?.categoryId) }
    var supplierId by remember { mutableStateOf<Int?>(productToEdit?.supplierId) }
    
    var categoryExpanded by remember { mutableStateOf(false) }
    var supplierExpanded by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (productToEdit == null) "Add Product" else "Edit Product") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            LinearProgressIndicator(
                progress = { step / 3f },
                modifier = Modifier.fillMaxWidth()
            )

            when (step) {
                1 -> {
                    Text("Step 1: Basic Details", style = MaterialTheme.typography.titleLarge)
                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        label = { Text("Product Name") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = sku,
                        onValueChange = { sku = it },
                        label = { Text("SKU") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = description,
                        onValueChange = { description = it },
                        label = { Text("Description") },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 3
                    )
                }
                2 -> {
                    Text("Step 2: Price & Inventory", style = MaterialTheme.typography.titleLarge)
                    OutlinedTextField(
                        value = unitPrice,
                        onValueChange = { unitPrice = it },
                        label = { Text("Unit Price ($)") },
                        modifier = Modifier.fillMaxWidth(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = stockLevel,
                        onValueChange = { stockLevel = it },
                        label = { Text("Initial Stock") },
                        modifier = Modifier.fillMaxWidth(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = barcode,
                        onValueChange = { barcode = it },
                        label = { Text("Barcode (Optional)") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                }
                3 -> {
                    Text("Step 3: Classification", style = MaterialTheme.typography.titleLarge)
                    
                    ExposedDropdownMenuBox(
                        expanded = categoryExpanded,
                        onExpandedChange = { categoryExpanded = it }
                    ) {
                        val catName = categories.find { it.id == categoryId }?.name ?: "Select Category"
                        OutlinedTextField(
                            value = catName,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Category") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = categoryExpanded) },
                            modifier = Modifier.menuAnchor().fillMaxWidth()
                        )
                        ExposedDropdownMenu(
                            expanded = categoryExpanded,
                            onDismissRequest = { categoryExpanded = false }
                        ) {
                            categories.forEach { category ->
                                DropdownMenuItem(
                                    text = { Text(category.name) },
                                    onClick = { 
                                        categoryId = category.id
                                        categoryExpanded = false 
                                    }
                                )
                            }
                        }
                    }

                    ExposedDropdownMenuBox(
                        expanded = supplierExpanded,
                        onExpandedChange = { supplierExpanded = it }
                    ) {
                        val supName = suppliers.find { it.id == supplierId }?.name ?: "Select Supplier"
                        OutlinedTextField(
                            value = supName,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Supplier") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = supplierExpanded) },
                            modifier = Modifier.menuAnchor().fillMaxWidth()
                        )
                        ExposedDropdownMenu(
                            expanded = supplierExpanded,
                            onDismissRequest = { supplierExpanded = false }
                        ) {
                            suppliers.forEach { supplier ->
                                DropdownMenuItem(
                                    text = { Text(supplier.name) },
                                    onClick = { 
                                        supplierId = supplier.id
                                        supplierExpanded = false 
                                    }
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                if (step > 1) {
                    OutlinedButton(onClick = { step-- }) { Text("Back") }
                } else {
                    Spacer(modifier = Modifier.weight(1f)) // pushes Next to the right
                }

                if (step < 3) {
                    Button(
                        onClick = { step++ },
                        enabled = when (step) {
                            1 -> name.isNotBlank() && sku.isNotBlank()
                            2 -> unitPrice.toDoubleOrNull() != null && stockLevel.toIntOrNull() != null
                            else -> true
                        }
                    ) { Text("Next") }
                } else {
                    Button(onClick = {
                        val product = Product(
                            id = productToEdit?.id ?: 0,
                            name = name,
                            sku = sku,
                            description = description,
                            unitPrice = unitPrice.toDoubleOrNull() ?: 0.0,
                            stockLevel = stockLevel.toIntOrNull() ?: 0,
                            barcode = barcode,
                            categoryId = categoryId,
                            supplierId = supplierId
                        )
                        onSave(product)
                    }) {
                        Text(if (productToEdit == null) "Create Product" else "Save Changes")
                    }
                }
            }
        }
    }
}
