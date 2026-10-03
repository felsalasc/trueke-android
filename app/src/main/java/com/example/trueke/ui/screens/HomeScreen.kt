package com.example.trueke.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.trueke.data.ProductRepository
import com.example.trueke.model.Product
import com.example.trueke.utils.filterProducts
import com.google.firebase.auth.FirebaseAuth

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen() {

    var products by remember {
        mutableStateOf<List<Product>>(emptyList())
    }

    var errorMessage by remember {
        mutableStateOf("")
    }

    var successMessage by remember {
        mutableStateOf("")
    }

    var expandedCategory by remember {
        mutableStateOf(false)
    }

    var selectedCategory by remember {
        mutableStateOf("Todas")
    }

    var onlyGoodCondition by remember {
        mutableStateOf(false)
    }

    var selectedDistance by remember {
        mutableStateOf(10)
    }

    var showProductDialog by remember {
        mutableStateOf(false)
    }

    var productToEdit by remember {
        mutableStateOf<Product?>(null)
    }

    var productToDelete by remember {
        mutableStateOf<Product?>(null)
    }

    val categories = listOf(
        "Todas",
        "Deportes",
        "Videojuegos",
        "Instrumentos",
        "Tecnología",
        "Fotografía",
        "Otros"
    )

    // --------------------------------------------------
    // READ - FIRESTORE
    // --------------------------------------------------

    DisposableEffect(Unit) {

        val listener =
            ProductRepository.listenProducts(

                onProductsChanged = { newProducts ->

                    products = newProducts
                    errorMessage = ""
                },

                onError = { message ->

                    errorMessage = message
                }
            )

        onDispose {
            listener.remove()
        }
    }

    // --------------------------------------------------
    // FILTROS
    // --------------------------------------------------

    val filteredProducts =
        filterProducts(products) { product ->

            val categoryMatches =
                selectedCategory == "Todas" ||
                        product.category == selectedCategory

            val distanceMatches =
                product.distanceKm <= selectedDistance

            val conditionMatches =
                !onlyGoodCondition ||
                        product.condition.contains(
                            "Buen",
                            ignoreCase = true
                        ) ||
                        product.condition.contains(
                            "Excelente",
                            ignoreCase = true
                        )

            categoryMatches &&
                    distanceMatches &&
                    conditionMatches
        }

    LazyVerticalGrid(

        columns = GridCells.Adaptive(
            minSize = 160.dp
        ),

        modifier = Modifier.padding(16.dp),

        horizontalArrangement =
            Arrangement.spacedBy(12.dp),

        verticalArrangement =
            Arrangement.spacedBy(12.dp)

    ) {

        // --------------------------------------------------
        // ENCABEZADO
        // --------------------------------------------------

        item(
            span = {
                GridItemSpan(maxLineSpan)
            }
        ) {

            Column {

                Text(
                    text = "TRUEKE",
                    style = MaterialTheme.typography.headlineMedium
                )

                Text(
                    text = "Productos cerca de ti",
                    style = MaterialTheme.typography.titleLarge
                )

                Spacer(
                    modifier = Modifier.height(6.dp)
                )

                Text(
                    text = "Trueke prioriza la comunicación visual y escrita entre usuarios.",
                    style = MaterialTheme.typography.bodyMedium
                )

                Text(
                    text = "La información importante siempre se presenta mediante texto para facilitar una experiencia accesible.",
                    style = MaterialTheme.typography.bodySmall
                )

                Spacer(
                    modifier = Modifier.height(16.dp)
                )

                Button(
                    onClick = {

                        productToEdit = null
                        showProductDialog = true
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {

                    Text("+ Publicar producto")
                }

                Spacer(
                    modifier = Modifier.height(12.dp)
                )

                if (successMessage.isNotEmpty()) {

                    Text(
                        text = successMessage,
                        color = MaterialTheme.colorScheme.primary
                    )

                    Spacer(
                        modifier = Modifier.height(8.dp)
                    )
                }

                if (errorMessage.isNotEmpty()) {

                    Text(
                        text = errorMessage,
                        color = MaterialTheme.colorScheme.error
                    )

                    Spacer(
                        modifier = Modifier.height(8.dp)
                    )
                }
            }
        }

        // --------------------------------------------------
        // CATEGORÍA
        // --------------------------------------------------

        item(
            span = {
                GridItemSpan(maxLineSpan)
            }
        ) {

            Column {

                Text(
                    text = "Categoría",
                    style = MaterialTheme.typography.titleMedium
                )

                Spacer(
                    modifier = Modifier.height(6.dp)
                )

                ExposedDropdownMenuBox(
                    expanded = expandedCategory,
                    onExpandedChange = {
                        expandedCategory = !expandedCategory
                    }
                ) {

                    OutlinedTextField(
                        value = selectedCategory,
                        onValueChange = {},
                        readOnly = true,
                        label = {
                            Text("Seleccionar categoría")
                        },
                        trailingIcon = {

                            ExposedDropdownMenuDefaults.TrailingIcon(
                                expanded = expandedCategory
                            )
                        },
                        modifier = Modifier
                            .menuAnchor()
                            .fillMaxWidth()
                    )

                    ExposedDropdownMenu(
                        expanded = expandedCategory,
                        onDismissRequest = {
                            expandedCategory = false
                        }
                    ) {

                        categories.forEach { category ->

                            DropdownMenuItem(
                                text = {
                                    Text(category)
                                },
                                onClick = {

                                    selectedCategory = category
                                    expandedCategory = false
                                }
                            )
                        }
                    }
                }

                Spacer(
                    modifier = Modifier.height(16.dp)
                )
            }
        }

        // --------------------------------------------------
        // ESTADO
        // --------------------------------------------------

        item(
            span = {
                GridItemSpan(maxLineSpan)
            }
        ) {

            Row(
                verticalAlignment =
                    Alignment.CenterVertically
            ) {

                Checkbox(
                    checked = onlyGoodCondition,
                    onCheckedChange = {
                        onlyGoodCondition = it
                    }
                )

                Text(
                    text =
                        "Mostrar solo productos en buen estado"
                )
            }
        }

        // --------------------------------------------------
        // DISTANCIA
        // --------------------------------------------------

        item(
            span = {
                GridItemSpan(maxLineSpan)
            }
        ) {

            Column {

                Spacer(
                    modifier = Modifier.height(12.dp)
                )

                Text(
                    text = "Distancia máxima",
                    style = MaterialTheme.typography.titleMedium
                )

                DistanceOption(
                    text = "2 km",
                    value = 2,
                    selectedDistance = selectedDistance,
                    onSelected = {
                        selectedDistance = it
                    }
                )

                DistanceOption(
                    text = "5 km",
                    value = 5,
                    selectedDistance = selectedDistance,
                    onSelected = {
                        selectedDistance = it
                    }
                )

                DistanceOption(
                    text = "10 km",
                    value = 10,
                    selectedDistance = selectedDistance,
                    onSelected = {
                        selectedDistance = it
                    }
                )

                Spacer(
                    modifier = Modifier.height(16.dp)
                )
            }
        }

        // --------------------------------------------------
        // RESUMEN
        // --------------------------------------------------

        item(
            span = {
                GridItemSpan(maxLineSpan)
            }
        ) {

            CatalogSummaryTable(
                totalProducts = products.size,
                visibleProducts =
                    filteredProducts.size,
                category = selectedCategory,
                distance = selectedDistance
            )
        }

        // --------------------------------------------------
        // PRODUCTOS
        // --------------------------------------------------

        item(
            span = {
                GridItemSpan(maxLineSpan)
            }
        ) {

            Column {

                Spacer(
                    modifier = Modifier.height(20.dp)
                )

                Text(
                    text = "Productos disponibles",
                    style = MaterialTheme.typography.titleLarge
                )

                Text(
                    text =
                        "${filteredProducts.size} productos encontrados",
                    style = MaterialTheme.typography.bodyMedium
                )

                Spacer(
                    modifier = Modifier.height(12.dp)
                )
            }
        }

        items(
            items = filteredProducts,
            key = { product ->
                product.id
            }
        ) { product ->

            ProductCard(
                product = product,

                onEdit = {

                    productToEdit = product
                    showProductDialog = true
                },

                onDelete = {

                    productToDelete = product
                }
            )
        }

        if (filteredProducts.isEmpty()) {

            item(
                span = {
                    GridItemSpan(maxLineSpan)
                }
            ) {

                Text(
                    text =
                        "No encontramos productos con estos filtros.",
                    style =
                        MaterialTheme.typography.bodyLarge,
                    modifier =
                        Modifier.padding(24.dp)
                )
            }
        }
    }

    // --------------------------------------------------
    // DIALOG CREAR / EDITAR
    // --------------------------------------------------

    if (showProductDialog) {

        ProductFormDialog(

            product = productToEdit,

            onDismiss = {

                showProductDialog = false
                productToEdit = null
            },

            onSave = { product ->

                successMessage = ""
                errorMessage = ""

                if (product.id.isBlank()) {

                    // CREATE
                    ProductRepository.addProduct(

                        product = product,

                        onSuccess = {

                            successMessage =
                                "Producto registrado correctamente"

                            showProductDialog = false
                            productToEdit = null
                        },

                        onError = { message ->

                            errorMessage = message
                        }
                    )

                } else {

                    // UPDATE
                    ProductRepository.updateProduct(

                        product = product,

                        onSuccess = {

                            successMessage =
                                "Producto actualizado correctamente"

                            showProductDialog = false
                            productToEdit = null
                        },

                        onError = { message ->

                            errorMessage = message
                        }
                    )
                }
            }
        )
    }

    // --------------------------------------------------
    // DIALOG ELIMINAR
    // --------------------------------------------------

    productToDelete?.let { product ->

        AlertDialog(

            onDismissRequest = {
                productToDelete = null
            },

            title = {
                Text("Eliminar producto")
            },

            text = {

                Text(
                    "¿Deseas eliminar \"${product.name}\"?"
                )
            },

            confirmButton = {

                Button(
                    onClick = {

                        ProductRepository.deleteProduct(

                            productId = product.id,

                            onSuccess = {

                                successMessage =
                                    "Producto eliminado correctamente"

                                productToDelete = null
                            },

                            onError = { message ->

                                errorMessage = message
                            }
                        )
                    }
                ) {

                    Text("Eliminar")
                }
            },

            dismissButton = {

                TextButton(
                    onClick = {
                        productToDelete = null
                    }
                ) {

                    Text("Cancelar")
                }
            }
        )
    }
}


// --------------------------------------------------
// FORMULARIO PRODUCTO
// --------------------------------------------------

@Composable
fun ProductFormDialog(
    product: Product?,
    onDismiss: () -> Unit,
    onSave: (Product) -> Unit
) {

    var name by remember(product?.id) {
        mutableStateOf(product?.name ?: "")
    }

    var description by remember(product?.id) {
        mutableStateOf(product?.description ?: "")
    }

    var category by remember(product?.id) {
        mutableStateOf(product?.category ?: "")
    }

    var condition by remember(product?.id) {
        mutableStateOf(product?.condition ?: "")
    }

    var referenceValue by remember(product?.id) {
        mutableStateOf(
            product
                ?.referenceValue
                ?.toString()
                ?: ""
        )
    }

    var distanceKm by remember(product?.id) {
        mutableStateOf(
            product
                ?.distanceKm
                ?.toString()
                ?: ""
        )
    }

    var errorMessage by remember {
        mutableStateOf("")
    }

    val currentUser =
        FirebaseAuth
            .getInstance()
            .currentUser

    AlertDialog(

        onDismissRequest = onDismiss,

        title = {

            Text(
                if (product == null) {
                    "Publicar producto"
                } else {
                    "Editar producto"
                }
            )
        },

        text = {

            Column {

                OutlinedTextField(
                    value = name,
                    onValueChange = {
                        name = it
                        errorMessage = ""
                    },
                    label = {
                        Text("Nombre")
                    },
                    modifier =
                        Modifier.fillMaxWidth()
                )

                Spacer(
                    modifier = Modifier.height(8.dp)
                )

                OutlinedTextField(
                    value = description,
                    onValueChange = {
                        description = it
                    },
                    label = {
                        Text("Descripción")
                    },
                    modifier =
                        Modifier.fillMaxWidth()
                )

                Spacer(
                    modifier = Modifier.height(8.dp)
                )

                OutlinedTextField(
                    value = category,
                    onValueChange = {
                        category = it
                    },
                    label = {
                        Text("Categoría")
                    },
                    modifier =
                        Modifier.fillMaxWidth()
                )

                Spacer(
                    modifier = Modifier.height(8.dp)
                )

                OutlinedTextField(
                    value = condition,
                    onValueChange = {
                        condition = it
                    },
                    label = {
                        Text("Estado")
                    },
                    placeholder = {
                        Text("Ej: Buen estado")
                    },
                    modifier =
                        Modifier.fillMaxWidth()
                )

                Spacer(
                    modifier = Modifier.height(8.dp)
                )

                OutlinedTextField(
                    value = referenceValue,
                    onValueChange = {
                        referenceValue = it
                    },
                    label = {
                        Text("Valor referencial")
                    },
                    keyboardOptions =
                        KeyboardOptions(
                            keyboardType =
                                KeyboardType.Number
                        ),
                    modifier =
                        Modifier.fillMaxWidth()
                )

                Spacer(
                    modifier = Modifier.height(8.dp)
                )

                OutlinedTextField(
                    value = distanceKm,
                    onValueChange = {
                        distanceKm = it
                    },
                    label = {
                        Text("Distancia en km")
                    },
                    keyboardOptions =
                        KeyboardOptions(
                            keyboardType =
                                KeyboardType.Decimal
                        ),
                    modifier =
                        Modifier.fillMaxWidth()
                )

                if (errorMessage.isNotEmpty()) {

                    Spacer(
                        modifier =
                            Modifier.height(8.dp)
                    )

                    Text(
                        text = errorMessage,
                        color =
                            MaterialTheme
                                .colorScheme
                                .error
                    )
                }
            }
        },

        confirmButton = {

            Button(
                onClick = {

                    when {

                        name.isBlank() -> {

                            errorMessage =
                                "Ingresa el nombre del producto"
                        }

                        description.isBlank() -> {

                            errorMessage =
                                "Ingresa una descripción"
                        }

                        category.isBlank() -> {

                            errorMessage =
                                "Ingresa una categoría"
                        }

                        condition.isBlank() -> {

                            errorMessage =
                                "Ingresa el estado del producto"
                        }

                        referenceValue
                            .toIntOrNull() == null -> {

                            errorMessage =
                                "Ingresa un valor referencial válido"
                        }

                        distanceKm
                            .replace(",", ".")
                            .toDoubleOrNull() == null -> {

                            errorMessage =
                                "Ingresa una distancia válida"
                        }

                        else -> {

                            val owner =
                                currentUser
                                    ?.email
                                    ?: "Usuario TRUEKE"

                            onSave(

                                Product(
                                    id =
                                        product?.id ?: "",

                                    name =
                                        name.trim(),

                                    description =
                                        description.trim(),

                                    category =
                                        category.trim(),

                                    condition =
                                        condition.trim(),

                                    referenceValue =
                                        referenceValue
                                            .toInt(),

                                    distanceKm =
                                        distanceKm
                                            .replace(
                                                ",",
                                                "."
                                            )
                                            .toDouble(),

                                    owner =
                                        product
                                            ?.owner
                                            ?.takeIf {
                                                it.isNotBlank()
                                            }
                                            ?: owner
                                )
                            )
                        }
                    }
                }
            ) {

                Text(
                    if (product == null) {
                        "Publicar"
                    } else {
                        "Guardar"
                    }
                )
            }
        },

        dismissButton = {

            TextButton(
                onClick = onDismiss
            ) {

                Text("Cancelar")
            }
        }
    )
}


// --------------------------------------------------
// OPCIÓN DISTANCIA
// --------------------------------------------------

@Composable
fun DistanceOption(
    text: String,
    value: Int,
    selectedDistance: Int,
    onSelected: (Int) -> Unit
) {

    Row(
        verticalAlignment =
            Alignment.CenterVertically
    ) {

        RadioButton(
            selected =
                selectedDistance == value,

            onClick = {
                onSelected(value)
            }
        )

        Text(
            text = text
        )
    }
}


// --------------------------------------------------
// RESUMEN CATÁLOGO
// --------------------------------------------------

@Composable
fun CatalogSummaryTable(
    totalProducts: Int,
    visibleProducts: Int,
    category: String,
    distance: Int
) {

    Card(
        modifier =
            Modifier.fillMaxWidth()
    ) {

        Column(
            modifier =
                Modifier.padding(16.dp)
        ) {

            Text(
                text = "Resumen del catálogo",
                style =
                    MaterialTheme
                        .typography
                        .titleMedium
            )

            Spacer(
                modifier =
                    Modifier.height(12.dp)
            )

            TableRow(
                title =
                    "Productos registrados",
                value =
                    totalProducts.toString()
            )

            TableRow(
                title =
                    "Productos encontrados",
                value =
                    visibleProducts.toString()
            )

            TableRow(
                title = "Categoría",
                value = category
            )

            TableRow(
                title = "Distancia",
                value = "Hasta $distance km"
            )
        }
    }
}


// --------------------------------------------------
// FILA RESUMEN
// --------------------------------------------------

@Composable
fun TableRow(
    title: String,
    value: String
) {

    Row(
        modifier =
            Modifier
                .fillMaxWidth()
                .padding(
                    vertical = 5.dp
                ),

        horizontalArrangement =
            Arrangement.SpaceBetween
    ) {

        Text(
            text = title,
            style =
                MaterialTheme
                    .typography
                    .bodyMedium
        )

        Text(
            text = value,
            style =
                MaterialTheme
                    .typography
                    .bodyMedium
        )
    }
}


// --------------------------------------------------
// TARJETA PRODUCTO
// --------------------------------------------------

@Composable
fun ProductCard(
    product: Product,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {

    Card(
        modifier =
            Modifier.fillMaxWidth()
    ) {

        Column(
            modifier =
                Modifier.padding(12.dp)
        ) {

            Text(
                text = product.name,
                style =
                    MaterialTheme
                        .typography
                        .titleMedium
            )

            Spacer(
                modifier =
                    Modifier.height(4.dp)
            )

            Text(
                text = product.category,
                style =
                    MaterialTheme
                        .typography
                        .labelLarge
            )

            Spacer(
                modifier =
                    Modifier.height(8.dp)
            )

            Text(
                text =
                    product.description,
                style =
                    MaterialTheme
                        .typography
                        .bodySmall
            )

            Spacer(
                modifier =
                    Modifier.height(8.dp)
            )

            Text(
                text =
                    "Estado: ${product.condition}",
                style =
                    MaterialTheme
                        .typography
                        .bodySmall
            )

            Text(
                text =
                    "Valor referencial: $${product.referenceValue}",
                style =
                    MaterialTheme
                        .typography
                        .bodySmall
            )

            Text(
                text =
                    "Distancia: ${product.distanceKm} km",
                style =
                    MaterialTheme
                        .typography
                        .bodySmall
            )

            Text(
                text =
                    "Publicado por: ${product.owner}",
                style =
                    MaterialTheme
                        .typography
                        .bodySmall
            )

            Spacer(
                modifier =
                    Modifier.height(12.dp)
            )

            Button(
                onClick = onEdit,
                modifier =
                    Modifier.fillMaxWidth()
            ) {

                Text("Editar")
            }

            TextButton(
                onClick = onDelete,
                modifier =
                    Modifier.fillMaxWidth()
            ) {

                Text("Eliminar")
            }

            Button(
                onClick = {
                    // Futuro chat interno
                },
                modifier =
                    Modifier.fillMaxWidth()
            ) {

                Text(
                    "Contactar por mensaje"
                )
            }
        }
    }
}