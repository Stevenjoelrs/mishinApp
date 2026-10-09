package com.mishin.feature.cats

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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items as gridItems
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.outlined.Pets
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mishin.core.domain.model.Cat
import com.mishin.core.ui.components.MishinTopBar
import com.mishin.feature.cats.components.AddEditCatSheet
import com.mishin.feature.cats.components.CatCard

@Composable
fun CatsScreen(
    onOpenDrawer: () -> Unit,
    viewModel: CatsViewModel = hiltViewModel()
) {
    val cats by viewModel.cats.collectAsStateWithLifecycle()

    var catSheetOpen by rememberSaveable { mutableStateOf(false) }
    var editingCatId by rememberSaveable { mutableStateOf<String?>(null) }
    var catToDelete by remember { mutableStateOf<Cat?>(null) }

    val editingCat = cats.find { it.id == editingCatId }

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
                text = "Lista de gatitos",
                style = MaterialTheme.typography.headlineLarge,
                color = MaterialTheme.colorScheme.onBackground
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Administra la lista de gatitos de la cafetería",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(20.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Adopciones",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier.weight(1f)
                )
                AddButton(
                    label = "Agregar gatito",
                    onClick = {
                        editingCatId = null
                        catSheetOpen = true
                    }
                )
            }
            Text(
                text = "Un hogar para él, un lugar para el próximo.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(14.dp))

            if (cats.isEmpty()) {
                CatsEmptyState(
                    modifier = Modifier.weight(1f),
                    icon = Icons.Outlined.Pets,
                    title = "No hay gatitos",
                    message = "Agrega un gatito con el botón de arriba."
                )
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    modifier = Modifier.weight(1f),
                    horizontalArrangement = Arrangement.spacedBy(14.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp),
                    contentPadding = PaddingValues(bottom = 8.dp)
                ) {
                    gridItems(cats, key = { it.id }) { cat ->
                        CatCard(
                            cat = cat,
                            onEdit = {
                                editingCatId = cat.id
                                catSheetOpen = true
                            },
                            onDelete = { catToDelete = cat },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(24.dp))
        }
    }

    if (catSheetOpen) {
        AddEditCatSheet(
            cat = editingCat,
            onDismiss = { catSheetOpen = false },
            onSave = { name, age, medicalCheck, tripleVaccine, sterilized, notes, imageUri ->
                viewModel.saveCat(
                    existing = editingCat,
                    name = name,
                    ageMonthsText = age,
                    medicalCheck = medicalCheck,
                    tripleVaccine = tripleVaccine,
                    sterilized = sterilized,
                    notes = notes,
                    imageUri = imageUri
                )
                catSheetOpen = false
            }
        )
    }

    catToDelete?.let { cat ->
        AlertDialog(
            onDismissRequest = { catToDelete = null },
            containerColor = MaterialTheme.colorScheme.surface,
            title = { Text("Eliminar gatito") },
            text = {
                Text("¿Eliminar \"${cat.name}\"? Esta acción no se puede deshacer.")
            },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.deleteCat(cat.id)
                    catToDelete = null
                }) {
                    Text(
                        text = "Eliminar",
                        color = MaterialTheme.colorScheme.error
                    )
                }
            },
            dismissButton = {
                TextButton(onClick = { catToDelete = null }) {
                    Text("Cancelar")
                }
            }
        )
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

@Composable
private fun CatsEmptyState(
    icon: ImageVector,
    title: String,
    message: String,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.fillMaxWidth(),
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
