package com.mishin.feature.menu

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items as gridItems
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.outlined.Category
import androidx.compose.material.icons.outlined.RestaurantMenu
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mishin.core.domain.model.MenuCategory
import com.mishin.core.domain.model.MenuItem
import com.mishin.core.ui.components.MishinTopBar
import com.mishin.feature.menu.components.AddEditCategorySheet
import com.mishin.feature.menu.components.AddEditProductSheet
import com.mishin.feature.menu.components.CategoryCard
import com.mishin.feature.menu.components.ProductCard

enum class MenuTab(val label: String) {
    CATEGORIES("Categorías"),
    PRODUCTS("Productos")
}

@Composable
fun MenuScreen(
    onOpenDrawer: () -> Unit,
    viewModel: MenuViewModel = hiltViewModel()
) {
    val categories by viewModel.categories.collectAsStateWithLifecycle()
    val products by viewModel.products.collectAsStateWithLifecycle()

    var tab by rememberSaveable { mutableStateOf(MenuTab.CATEGORIES) }
    var productFilterId by rememberSaveable { mutableStateOf<String?>(null) }

    var categorySheetOpen by rememberSaveable { mutableStateOf(false) }
    var editingCategoryId by rememberSaveable { mutableStateOf<String?>(null) }
    var productSheetOpen by rememberSaveable { mutableStateOf(false) }
    var editingProductId by rememberSaveable { mutableStateOf<String?>(null) }

    var categoryToDelete by remember { mutableStateOf<MenuCategory?>(null) }
    var productToDelete by remember { mutableStateOf<MenuItem?>(null) }

    val editingCategory = categories.find { it.id == editingCategoryId }
    val editingProduct = products.find { it.id == editingProductId }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = { MishinTopBar(onNavigationClick = onOpenDrawer) }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .padding(horizontal = 20.dp)
        ) {
            Text(
                text = "Gestión de menú",
                style = MaterialTheme.typography.headlineLarge,
                color = MaterialTheme.colorScheme.onBackground
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Administra las categorías y productos de la cafetería",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(20.dp))
            MenuSegmentedControl(selected = tab, onSelect = { tab = it })
            Spacer(modifier = Modifier.height(20.dp))

            when (tab) {
                MenuTab.CATEGORIES -> CategoriesSection(
                    categories = categories,
                    onAdd = {
                        editingCategoryId = null
                        categorySheetOpen = true
                    },
                    onEdit = { category ->
                        editingCategoryId = category.id
                        categorySheetOpen = true
                    },
                    onDelete = { categoryToDelete = it }
                )

                MenuTab.PRODUCTS -> ProductsSection(
                    categories = categories,
                    products = products,
                    selectedCategoryId = productFilterId,
                    onFilterSelected = { productFilterId = it },
                    onAdd = {
                        editingProductId = null
                        productSheetOpen = true
                    },
                    onEdit = { product ->
                        editingProductId = product.id
                        productSheetOpen = true
                    },
                    onDelete = { productToDelete = it }
                )
            }
        }
    }

    if (categorySheetOpen) {
        AddEditCategorySheet(
            category = editingCategory,
            onDismiss = { categorySheetOpen = false },
            onSave = { name, description ->
                viewModel.saveCategory(editingCategory, name, description)
                categorySheetOpen = false
            }
        )
    }

    if (productSheetOpen) {
        AddEditProductSheet(
            product = editingProduct,
            categories = categories,
            onDismiss = { productSheetOpen = false },
            onSave = { name, price, categoryId, description, imageUri ->
                viewModel.saveProduct(
                    existing = editingProduct,
                    name = name,
                    price = price,
                    categoryId = categoryId,
                    description = description,
                    imageUri = imageUri
                )
                productSheetOpen = false
            }
        )
    }

    categoryToDelete?.let { category ->
        AlertDialog(
            onDismissRequest = { categoryToDelete = null },
            containerColor = MaterialTheme.colorScheme.surface,
            title = { Text("Eliminar categoría") },
            text = {
                Text("¿Eliminar \"${category.name}\"? Esta acción no se puede deshacer.")
            },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.deleteCategory(category.id)
                    categoryToDelete = null
                }) {
                    Text(
                        text = "Eliminar",
                        color = MaterialTheme.colorScheme.error
                    )
                }
            },
            dismissButton = {
                TextButton(onClick = { categoryToDelete = null }) {
                    Text("Cancelar")
                }
            }
        )
    }

    productToDelete?.let { product ->
        AlertDialog(
            onDismissRequest = { productToDelete = null },
            containerColor = MaterialTheme.colorScheme.surface,
            title = { Text("Eliminar producto") },
            text = {
                Text("¿Eliminar \"${product.name}\"? Esta acción no se puede deshacer.")
            },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.deleteProduct(product.id)
                    productToDelete = null
                }) {
                    Text(
                        text = "Eliminar",
                        color = MaterialTheme.colorScheme.error
                    )
                }
            },
            dismissButton = {
                TextButton(onClick = { productToDelete = null }) {
                    Text("Cancelar")
                }
            }
        )
    }
}

@Composable
private fun MenuSegmentedControl(
    selected: MenuTab,
    onSelect: (MenuTab) -> Unit
) {
    val colors = MaterialTheme.colorScheme

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(50))
            .background(colors.onBackground)
            .padding(4.dp)
    ) {
        MenuTab.entries.forEach { item ->
            val active = item == selected
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(50))
                    .background(if (active) colors.background else Color.Transparent)
                    .clickable { onSelect(item) }
                    .padding(vertical = 12.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = item.label,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = if (active) FontWeight.Bold else FontWeight.SemiBold,
                    color = if (active) colors.onBackground else colors.background
                )
            }
        }
    }
}

@Composable
private fun CategoriesSection(
    categories: List<MenuCategory>,
    onAdd: () -> Unit,
    onEdit: (MenuCategory) -> Unit,
    onDelete: (MenuCategory) -> Unit
) {
    Column(modifier = Modifier.fillMaxSize()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End,
            verticalAlignment = Alignment.CenterVertically
        ) {
            AddButton(label = "Nueva categoría", onClick = onAdd)
        }
        Spacer(modifier = Modifier.height(16.dp))

        if (categories.isEmpty()) {
            EmptyState(
                icon = Icons.Outlined.Category,
                title = "No hay categorías",
                message = "Crea una categoría con el botón de arriba."
            )
        } else {
            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(14.dp),
                contentPadding = PaddingValues(bottom = 24.dp)
            ) {
                items(categories, key = { it.id }) { category ->
                    CategoryCard(
                        category = category,
                        onEdit = { onEdit(category) },
                        onDelete = { onDelete(category) }
                    )
                }
            }
        }
    }
}

@Composable
private fun ProductsSection(
    categories: List<MenuCategory>,
    products: List<MenuItem>,
    selectedCategoryId: String?,
    onFilterSelected: (String?) -> Unit,
    onAdd: () -> Unit,
    onEdit: (MenuItem) -> Unit,
    onDelete: (MenuItem) -> Unit
) {
    val effectiveFilterId = categories.find { it.id == selectedCategoryId }?.id
    val filteredProducts = if (effectiveFilterId == null) {
        products
    } else {
        products.filter { it.categoryId == effectiveFilterId }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            CategoryFilterDropdown(
                categories = categories,
                selectedCategoryId = effectiveFilterId,
                onSelected = onFilterSelected
            )
            Spacer(modifier = Modifier.weight(1f))
            AddButton(label = "Nuevo producto", onClick = onAdd)
        }
        Spacer(modifier = Modifier.height(20.dp))
        Text(
            text = "Productos",
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.onBackground
        )
        Text(
            text = "Gestiona los productos del menú",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(14.dp))

        if (filteredProducts.isEmpty()) {
            EmptyState(
                icon = Icons.Outlined.RestaurantMenu,
                title = "No hay productos",
                message = "Agrega un producto con el botón de arriba."
            )
        } else {
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier.weight(1f),
                horizontalArrangement = Arrangement.spacedBy(14.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp),
                contentPadding = PaddingValues(bottom = 24.dp)
            ) {
                gridItems(filteredProducts, key = { it.id }) { product ->
                    ProductCard(
                        product = product,
                        onEdit = { onEdit(product) },
                        onDelete = { onDelete(product) },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    }
}

@Composable
private fun AddButton(
    label: String,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        shape = RoundedCornerShape(50),
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.secondary,
            contentColor = MaterialTheme.colorScheme.onSecondary
        )
    ) {
        Icon(
            imageVector = Icons.Default.Add,
            contentDescription = null,
            modifier = Modifier.size(18.dp)
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.SemiBold
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CategoryFilterDropdown(
    categories: List<MenuCategory>,
    selectedCategoryId: String?,
    onSelected: (String?) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }
    val label = categories.firstOrNull { it.id == selectedCategoryId }?.name ?: "Todas"

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = it },
        modifier = modifier
    ) {
        OutlinedTextField(
            value = label,
            onValueChange = {},
            readOnly = true,
            singleLine = true,
            shape = RoundedCornerShape(14.dp),
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
            },
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = MaterialTheme.colorScheme.surface,
                unfocusedContainerColor = MaterialTheme.colorScheme.surface
            ),
            modifier = Modifier
                .menuAnchor(MenuAnchorType.PrimaryNotEditable)
                .width(150.dp)
        )
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.exposedDropdownSize()
        ) {
            DropdownMenuItem(
                text = { Text("Todas") },
                onClick = {
                    onSelected(null)
                    expanded = false
                }
            )
            categories.forEach { category ->
                DropdownMenuItem(
                    text = { Text(category.name) },
                    onClick = {
                        onSelected(category.id)
                        expanded = false
                    }
                )
            }
        }
    }
}

@Composable
private fun EmptyState(
    icon: ImageVector,
    title: String,
    message: String
) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(48.dp),
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onBackground
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = message,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
