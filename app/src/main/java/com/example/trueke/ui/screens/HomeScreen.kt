package com.example.trueke.ui.screens

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuAnchorType
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.trueke.data.ProductDataSource
import com.example.trueke.data.ProductRepository
import com.example.trueke.model.Product
import com.example.trueke.utils.filterProducts
import com.example.trueke.utils.isValidEmail
import com.example.trueke.utils.parseDistanceKm
import com.example.trueke.utils.parseReferenceValue
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

private val productCategories = listOf(
    "Deportes", "Videojuegos", "Instrumentos", "Tecnología", "Fotografía", "Otros"
)
private val productConditions = listOf("Excelente", "Buen estado", "Usado")
private val productDistances = listOf(2, 5, 10)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onLogout: () -> Unit,
    productDataSource: ProductDataSource = ProductRepository
) {

    val userUid = FirebaseAuth.getInstance().currentUser?.uid

    var userName by remember(userUid) {
        mutableStateOf("")
    }

    var profileMessage by remember(userUid) {
        mutableStateOf("Cargando perfil...")
    }

    DisposableEffect(userUid) {
        val profileListener = userUid?.let { uid ->
            FirebaseFirestore.getInstance()
                .collection("users")
                .document(uid)
                .addSnapshotListener { snapshot, exception ->
                    if (exception != null) {
                        userName = ""
                        profileMessage = "No fue posible cargar tu perfil."
                    } else {
                        userName = (snapshot?.get("name") as? String)
                            ?.trim().orEmpty()
                        profileMessage = if (userName.isBlank()) {
                            "Tu perfil no tiene un nombre registrado."
                        } else {
                            ""
                        }
                    }
                }
        }

        onDispose {
            profileListener?.remove()
        }
    }

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

    var showCommunicationDialog by remember { mutableStateOf(false) }

    var isSavingProduct by remember {
        mutableStateOf(false)
    }

    // Se invoca al recibir el resultado, no al iniciar la operación asíncrona.
    fun completeProductSave(handleResult: () -> Unit) {
        try {
            handleResult()
        } finally {
            isSavingProduct = false
        }
    }

    var productSaveError by remember {
        mutableStateOf("")
    }

    var productToEdit by remember {
        mutableStateOf<Product?>(null)
    }

    var productToDelete by remember {
        mutableStateOf<Product?>(null)
    }

    val categories = listOf("Todas") + productCategories

    // --------------------------------------------------
    // READ - FIRESTORE
    // --------------------------------------------------

    DisposableEffect(productDataSource) {

        val listener =
            productDataSource.listenProducts(

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
                    text = if (userName.isNotBlank()) "Hola, $userName" else "Hola",
                    style = MaterialTheme.typography.titleLarge
                )

                if (profileMessage.isNotEmpty()) {
                    Text(
                        text = profileMessage,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }

                TextButton(onClick = onLogout) {
                    Text("Cerrar sesión")
                }

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
                        productSaveError = ""
                        showProductDialog = true
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {

                    Text("+ Publicar producto")
                }

                TextButton(
                    onClick = { showCommunicationDialog = true },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Comunicación accesible")
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
                            .menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable)
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

                productDistances.forEach { distance ->
                    DistanceOption(
                        text = "$distance km",
                        value = distance,
                        selectedDistance = selectedDistance,
                        onSelected = { selectedDistance = it }
                    )
                }

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
                canManage = product.isOwnedBy(userUid),

                onEdit = {

                    productToEdit = product
                    productSaveError = ""
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

    if (showCommunicationDialog) {
        CommunicationDialog(onDismiss = { showCommunicationDialog = false })
    }

    if (showProductDialog) {

        ProductFormDialog(

            product = productToEdit,
            isSaving = isSavingProduct,
            saveError = productSaveError,

            onDismiss = {

                if (!isSavingProduct) {
                    showProductDialog = false
                    productToEdit = null
                    productSaveError = ""
                }
            },

            onSave = saveProduct@ { product ->

                if (isSavingProduct) return@saveProduct
                isSavingProduct = true
                productSaveError = ""

                successMessage = ""
                errorMessage = ""

                if (product.id.isBlank()) {

                    // CREATE
                    productDataSource.addProduct(

                        product = product,

                        onSuccess = {
                            completeProductSave {
                                successMessage = "Producto registrado correctamente"
                                showProductDialog = false
                                productToEdit = null
                            }
                        },

                        onError = { message ->
                            completeProductSave {
                                productSaveError = message
                                errorMessage = message
                            }
                        }
                    )

                } else {

                    // UPDATE
                    productDataSource.updateProduct(

                        product = product,

                        onSuccess = {
                            completeProductSave {
                                successMessage = "Producto actualizado correctamente"
                                showProductDialog = false
                                productToEdit = null
                            }
                        },

                        onError = { message ->
                            completeProductSave {
                                productSaveError = message
                                errorMessage = message
                            }
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

                        productDataSource.deleteProduct(

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
    isSaving: Boolean,
    saveError: String,
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

        onDismissRequest = {
            if (!isSaving) onDismiss()
        },

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

            Column(
                modifier = Modifier.verticalScroll(rememberScrollState())
            ) {

                OutlinedTextField(
                    value = name,
                    enabled = !isSaving,
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
                    enabled = !isSaving,
                    onValueChange = {
                        description = it
                        errorMessage = ""
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

                ProductDropdownField(
                    value = category,
                    enabled = !isSaving,
                    options = productCategories,
                    onSelected = {
                        category = it
                        errorMessage = ""
                    },
                    label = "Categoría"
                )

                Spacer(
                    modifier = Modifier.height(8.dp)
                )

                ProductDropdownField(
                    value = condition,
                    enabled = !isSaving,
                    options = productConditions,
                    onSelected = {
                        condition = it
                        errorMessage = ""
                    },
                    label = "Estado"
                )

                Spacer(
                    modifier = Modifier.height(8.dp)
                )

                OutlinedTextField(
                    value = referenceValue,
                    enabled = !isSaving,
                    onValueChange = {
                        referenceValue = it
                        errorMessage = ""
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

                ProductDropdownField(
                    value = distanceKm,
                    enabled = !isSaving,
                    options = productDistances.map { it.toDouble().toString() },
                    onSelected = {
                        distanceKm = it
                        errorMessage = ""
                    },
                    label = "Distancia",
                    optionLabel = { "${it.removeSuffix(".0")} km" }
                )

                val visibleError = errorMessage.ifBlank { saveError }
                if (visibleError.isNotEmpty()) {

                    Spacer(
                        modifier =
                            Modifier.height(8.dp)
                    )

                    Text(
                        text = visibleError,
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
                enabled = !isSaving,
                onClick = save@ {

                    if (isSaving) return@save
                    val parsedReferenceValue = parseReferenceValue(referenceValue)
                    val parsedDistanceKm = parseDistanceKm(distanceKm)

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

                        parsedReferenceValue == null -> {

                            errorMessage =
                                "Ingresa un entero entre 0 y 2147483647"
                        }

                        parsedDistanceKm == null -> {

                            errorMessage =
                                "Ingresa una distancia finita mayor o igual a 0"
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

                                    referenceValue = parsedReferenceValue,

                                    distanceKm = parsedDistanceKm,

                                    owner =
                                        product
                                            ?.owner
                                            ?.takeIf {
                                                it.isNotBlank()
                                            }
                                            ?: owner,
                                    ownerUid = product?.ownerUid
                                        ?: currentUser?.uid.orEmpty()
                                )
                            )
                        }
                    }
                }
            ) {

                Text(
                    if (isSaving) {
                        "Guardando..."
                    } else if (product == null) {
                        "Publicar"
                    } else {
                        "Guardar"
                    }
                )
            }
        },

        dismissButton = {

            TextButton(
                onClick = onDismiss,
                enabled = !isSaving
            ) {

                Text("Cancelar")
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ProductDropdownField(
    value: String,
    options: List<String>,
    label: String,
    enabled: Boolean,
    onSelected: (String) -> Unit,
    optionLabel: (String) -> String = { it }
) {
    var expanded by remember { mutableStateOf(false) }
    // Al editar, conserva valores anteriores que no pertenecen a las nuevas opciones.
    val availableOptions = if (value.isNotBlank() && value !in options) {
        options + value
    } else {
        options
    }

    ExposedDropdownMenuBox(
        expanded = expanded && enabled,
        onExpandedChange = { if (enabled) expanded = it }
    ) {
        OutlinedTextField(
            value = if (value.isBlank()) "" else optionLabel(value),
            onValueChange = {},
            readOnly = true,
            enabled = enabled,
            label = { Text(label) },
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded && enabled)
            },
            modifier = Modifier
                .menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable, enabled)
                .fillMaxWidth()
        )
        ExposedDropdownMenu(
            expanded = expanded && enabled,
            onDismissRequest = { expanded = false }
        ) {
            availableOptions.forEach { option ->
                DropdownMenuItem(
                    text = { Text(optionLabel(option)) },
                    onClick = {
                        onSelected(option)
                        expanded = false
                    }
                )
            }
        }
    }
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
    canManage: Boolean,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    val context = LocalContext.current
    var contactError by remember(product.id, product.owner) { mutableStateOf("") }

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

            if (canManage) {
                Button(
                    onClick = onEdit,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Editar")
                }

                TextButton(
                    onClick = onDelete,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Eliminar")
                }
            }

            if (!canManage) {
                Button(
                    onClick = contact@ {
                        contactError = ""
                        val recipient = product.owner.trim()
                        if (!recipient.isValidEmail()) {
                            contactError = "Este producto no tiene un correo de contacto válido."
                            return@contact
                        }

                        val subject = "TRUEKE: consulta por ${product.name}"
                        val message = "Hola, me interesa tu producto ${product.name}. " +
                            "¿Está disponible para un trueque?"
                        val emailIntent = Intent(Intent.ACTION_SENDTO).apply {
                            data = Uri.parse(
                                "mailto:${Uri.encode(recipient)}" +
                                    "?subject=${Uri.encode(subject)}&body=${Uri.encode(message)}"
                            )
                            putExtra(Intent.EXTRA_EMAIL, arrayOf(recipient))
                            putExtra(Intent.EXTRA_SUBJECT, subject)
                            putExtra(Intent.EXTRA_TEXT, message)
                        }
                        try {
                            context.startActivity(emailIntent)
                        } catch (_: ActivityNotFoundException) {
                            contactError = "No hay una aplicación de correo disponible. " +
                                "Instala o habilita una para contactar al publicador."
                        } catch (_: SecurityException) {
                            contactError = "No se pudo abrir la aplicación de correo."
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Contactar por correo")
                }

                if (contactError.isNotBlank()) {
                    Text(
                        text = contactError,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        }
    }
}
