package com.mishin.feature.games

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
import androidx.compose.material.icons.outlined.Extension
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
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mishin.core.domain.model.BoardGame
import com.mishin.core.ui.components.MishinTopBar
import com.mishin.feature.games.components.AddEditGameSheet
import com.mishin.feature.games.components.BoardGameCard
import com.mishin.feature.games.components.FormField
import com.mishin.feature.games.components.KaraokeCard
import com.mishin.feature.games.components.PromoBannerCard

@Composable
fun GamesScreen(
    onOpenDrawer: () -> Unit,
    viewModel: GamesViewModel = hiltViewModel()
) {
    val games by viewModel.games.collectAsStateWithLifecycle()
    val karaokePrice by viewModel.karaokePrice.collectAsStateWithLifecycle()
    val karaokePeriod by viewModel.karaokePeriodMinutes.collectAsStateWithLifecycle()
    val karaokeDescription by viewModel.karaokeDescription.collectAsStateWithLifecycle()
    val gamesPromo by viewModel.gamesPromo.collectAsStateWithLifecycle()

    var gameSheetOpen by rememberSaveable { mutableStateOf(false) }
    var editingGameId by rememberSaveable { mutableStateOf<String?>(null) }
    var gameToDelete by remember { mutableStateOf<BoardGame?>(null) }
    var karaokeDialogOpen by rememberSaveable { mutableStateOf(false) }
    var promoDialogOpen by rememberSaveable { mutableStateOf(false) }

    val editingGame = games.find { it.id == editingGameId }

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
                text = "Karaoke y juegos",
                style = MaterialTheme.typography.headlineLarge,
                color = MaterialTheme.colorScheme.onBackground
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Gestiona el karaoke y los juegos de mesa",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(20.dp))

            KaraokeCard(
                priceBs = karaokePrice,
                periodMinutes = karaokePeriod,
                description = karaokeDescription,
                onEdit = { karaokeDialogOpen = true }
            )
            Spacer(modifier = Modifier.height(20.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Juegos de mesa",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier.weight(1f)
                )
                AddButton(
                    label = "Agregar juego",
                    onClick = {
                        editingGameId = null
                        gameSheetOpen = true
                    }
                )
            }
            Spacer(modifier = Modifier.height(14.dp))

            if (games.isEmpty()) {
                GamesEmptyState(
                    modifier = Modifier.weight(1f),
                    icon = Icons.Outlined.Extension,
                    title = "No hay juegos",
                    message = "Agrega un juego con el botón de arriba."
                )
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    modifier = Modifier.weight(1f),
                    horizontalArrangement = Arrangement.spacedBy(14.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp),
                    contentPadding = PaddingValues(bottom = 8.dp)
                ) {
                    gridItems(games, key = { it.id }) { game ->
                        BoardGameCard(
                            game = game,
                            onEdit = {
                                editingGameId = game.id
                                gameSheetOpen = true
                            },
                            onDelete = { gameToDelete = game },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            PromoBannerCard(
                text = gamesPromo,
                onEdit = { promoDialogOpen = true }
            )
            Spacer(modifier = Modifier.height(24.dp))
        }
    }

    if (gameSheetOpen) {
        AddEditGameSheet(
            game = editingGame,
            onDismiss = { gameSheetOpen = false },
            onSave = { name, min, max, duration, difficulty, status, missingPieces, imageUri ->
                viewModel.saveGame(
                    existing = editingGame,
                    name = name,
                    minPlayers = min,
                    maxPlayers = max,
                    durationMinutes = duration,
                    difficulty = difficulty,
                    status = status,
                    missingPieces = missingPieces,
                    imageUri = imageUri
                )
                gameSheetOpen = false
            }
        )
    }

    gameToDelete?.let { game ->
        AlertDialog(
            onDismissRequest = { gameToDelete = null },
            containerColor = MaterialTheme.colorScheme.surface,
            title = { Text("Eliminar juego") },
            text = {
                Text("¿Eliminar \"${game.name}\"? Esta acción no se puede deshacer.")
            },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.deleteGame(game.id)
                    gameToDelete = null
                }) {
                    Text(
                        text = "Eliminar",
                        color = MaterialTheme.colorScheme.error
                    )
                }
            },
            dismissButton = {
                TextButton(onClick = { gameToDelete = null }) {
                    Text("Cancelar")
                }
            }
        )
    }

    if (karaokeDialogOpen) {
        KaraokeEditDialog(
            price = karaokePrice,
            periodMinutes = karaokePeriod,
            description = karaokeDescription,
            onDismiss = { karaokeDialogOpen = false },
            onSave = { newPrice, newPeriod, newDescription ->
                viewModel.saveKaraoke(newPrice, newPeriod, newDescription)
                karaokeDialogOpen = false
            }
        )
    }

    if (promoDialogOpen) {
        PromoEditDialog(
            text = gamesPromo,
            onDismiss = { promoDialogOpen = false },
            onSave = { newText ->
                viewModel.savePromo(newText)
                promoDialogOpen = false
            }
        )
    }
}

@Composable
private fun KaraokeEditDialog(
    price: String,
    periodMinutes: String,
    description: String,
    onDismiss: () -> Unit,
    onSave: (price: String, periodMinutes: String, description: String) -> Unit
) {
    var priceText by rememberSaveable { mutableStateOf(price) }
    var periodText by rememberSaveable { mutableStateOf(periodMinutes) }
    var descriptionText by rememberSaveable { mutableStateOf(description) }

    val parsedPrice = priceText.trim().replace(',', '.').toDoubleOrNull()
    val parsedPeriod = periodText.trim().toIntOrNull()
    val canSave = parsedPrice != null && parsedPrice >= 0 &&
        parsedPeriod != null && parsedPeriod >= 1 &&
        descriptionText.isNotBlank()

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = MaterialTheme.colorScheme.surface,
        title = {
            Text(
                text = "Editar karaoke",
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column {
                FormField(
                    label = "Precio Bs",
                    value = priceText,
                    onValueChange = { priceText = it },
                    keyboardType = KeyboardType.Decimal,
                    imeAction = ImeAction.Next,
                    placeholder = "0"
                )
                Spacer(modifier = Modifier.height(16.dp))
                FormField(
                    label = "Duración (min)",
                    value = periodText,
                    onValueChange = { periodText = it },
                    keyboardType = KeyboardType.Number,
                    imeAction = ImeAction.Next,
                    placeholder = "0"
                )
                Spacer(modifier = Modifier.height(16.dp))
                FormField(
                    label = "Descripción",
                    value = descriptionText,
                    onValueChange = { descriptionText = it },
                    singleLine = false,
                    maxLines = 4
                )
            }
        },
        confirmButton = {
            TextButton(
                enabled = canSave,
                onClick = {
                    onSave(
                        priceText.trim().replace(',', '.'),
                        periodText.trim(),
                        descriptionText.trim()
                    )
                }
            ) {
                Text("Guardar")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}

@Composable
private fun PromoEditDialog(
    text: String,
    onDismiss: () -> Unit,
    onSave: (text: String) -> Unit
) {
    var textValue by rememberSaveable { mutableStateOf(text) }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = MaterialTheme.colorScheme.surface,
        title = {
            Text(
                text = "Editar promoción",
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            FormField(
                label = "Texto de la promoción",
                value = textValue,
                onValueChange = { textValue = it },
                singleLine = false,
                maxLines = 4
            )
        },
        confirmButton = {
            TextButton(
                enabled = textValue.isNotBlank(),
                onClick = { onSave(textValue.trim()) }
            ) {
                Text("Guardar")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
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
private fun GamesEmptyState(
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
