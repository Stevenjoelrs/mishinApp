package com.mishin.core.data.mapper

import com.mishin.core.database.entity.BoardGameEntity
import com.mishin.core.database.entity.CatEntity
import com.mishin.core.database.entity.GameLoanEntity
import com.mishin.core.database.entity.InventoryItemEntity
import com.mishin.core.database.entity.MenuCategoryEntity
import com.mishin.core.database.entity.MenuItemEntity
import com.mishin.core.database.entity.RecipeLineEntity
import com.mishin.core.database.entity.StockMovementEntity
import com.mishin.core.domain.model.BaseUnit
import com.mishin.core.domain.model.BoardGame
import com.mishin.core.domain.model.Cat
import com.mishin.core.domain.model.CatSex
import com.mishin.core.domain.model.CatStatus
import com.mishin.core.domain.model.GameDifficulty
import com.mishin.core.domain.model.GameLoan
import com.mishin.core.domain.model.GameStatus
import com.mishin.core.domain.model.InventoryItem
import com.mishin.core.domain.model.ItemCategory
import com.mishin.core.domain.model.MenuCategory
import com.mishin.core.domain.model.MenuItem
import com.mishin.core.domain.model.MovementType
import com.mishin.core.domain.model.RecipeLine
import com.mishin.core.domain.model.StockMovement
import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * Round-trip coverage for every domain <-> entity mapper, using fully
 * populated fixtures so a swapped or missing field breaks a test.
 */
class MappersTest {

    private val cat = Cat(
        id = "cat_1",
        locationId = "loc_1",
        name = "Misu",
        ageMonths = 7,
        sex = CatSex.FEMALE,
        photoUri = "content://photos/1",
        status = CatStatus.ADOPTED,
        intakeDate = "2026-01-02T10:00:00Z",
        notes = "Cariñosa",
        medicalCheck = true,
        tripleVaccine = true,
        sterilized = false,
        createdAt = "2026-01-01T00:00:00Z",
        updatedAt = "2026-01-03T00:00:00Z",
        deletedAt = "2026-01-04T00:00:00Z",
        syncedAt = "2026-01-05T00:00:00Z"
    )

    private val boardGame = BoardGame(
        id = "game_1",
        locationId = "loc_1",
        name = "Catan",
        minPlayers = 2,
        maxPlayers = 4,
        durationMinutes = 90,
        difficulty = GameDifficulty.HARD,
        status = GameStatus.MAINTENANCE,
        missingPieces = "1 ficha",
        imageUri = null,
        createdAt = "2026-01-01T00:00:00Z",
        updatedAt = "2026-01-02T00:00:00Z",
        deletedAt = null,
        syncedAt = null
    )

    private val gameLoan = GameLoan(
        id = "loan_1",
        locationId = "loc_1",
        boardGameId = "game_1",
        tableReference = "T3",
        startTime = "2026-01-01T10:00:00Z",
        endTime = null,
        createdAt = "2026-01-01T10:00:00Z",
        syncedAt = null
    )

    private val menuCategory = MenuCategory(
        id = "cat_menu_1",
        locationId = "loc_1",
        name = "Bebidas",
        description = "Cafés y jugos",
        sortOrder = 4,
        createdAt = "2026-01-01T00:00:00Z",
        updatedAt = "2026-01-02T00:00:00Z",
        deletedAt = null,
        syncedAt = "2026-01-03T00:00:00Z"
    )

    private val menuItem = MenuItem(
        id = "item_1",
        locationId = "loc_1",
        name = "Latte",
        price = 12.5,
        categoryId = "cat_menu_1",
        description = "Con leche",
        imageUri = "content://images/latte",
        isActive = false,
        createdAt = "2026-01-01T00:00:00Z",
        updatedAt = "2026-01-02T00:00:00Z",
        deletedAt = null,
        syncedAt = null
    )

    private val recipeLine = RecipeLine(
        id = "recipe_1",
        menuItemId = "item_1",
        inventoryItemId = "inv_1",
        quantityUsed = 0.018,
        createdAt = "2026-01-01T00:00:00Z",
        updatedAt = "2026-01-01T00:00:00Z"
    )

    private val inventoryItem = InventoryItem(
        id = "inv_1",
        locationId = "loc_1",
        name = "Café",
        category = ItemCategory.CAFE,
        baseUnit = BaseUnit.GRAMS,
        maxStock = 5000.0,
        alertThresholdPercent = 25,
        supplierId = "sup_1",
        costPerUnit = 0.08,
        notes = "Tostado medio",
        createdAt = "2026-01-01T00:00:00Z",
        updatedAt = "2026-01-02T00:00:00Z",
        deletedAt = null,
        syncedAt = "2026-01-03T00:00:00Z"
    )

    private val stockMovement = StockMovement(
        id = "mov_1",
        locationId = "loc_1",
        inventoryItemId = "inv_1",
        quantity = -2.5,
        type = MovementType.REVERSAL,
        referenceOrderId = "order_1",
        note = "Pedido cancelado",
        createdAt = "2026-01-02T00:00:00Z",
        syncedAt = null
    )

    @Test
    fun `cat round-trips through its entity`() {
        assertEquals(cat, cat.toEntity().toDomain())
        assertEquals(catEntityFixture(), cat.toEntity())
        assertEquals(cat, catEntityFixture().toDomain())
    }

    @Test
    fun `board game round-trips through its entity`() {
        assertEquals(boardGame, boardGame.toEntity().toDomain())
        assertEquals(boardGame.toEntity(), boardGameEntityFixture())
    }

    @Test
    fun `game loan round-trips through its entity`() {
        assertEquals(gameLoan, gameLoan.toEntity().toDomain())
        assertEquals(gameLoan.toEntity(), gameLoanEntityFixture())
    }

    @Test
    fun `menu category round-trips through its entity`() {
        assertEquals(menuCategory, menuCategory.toEntity().toDomain())
        assertEquals(menuCategory.toEntity(), menuCategoryEntityFixture())
    }

    @Test
    fun `menu item round-trips through its entity`() {
        assertEquals(menuItem, menuItem.toEntity().toDomain())
        assertEquals(menuItem.toEntity(), menuItemEntityFixture())
    }

    @Test
    fun `recipe line round-trips through its entity`() {
        assertEquals(recipeLine, recipeLine.toEntity().toDomain())
        assertEquals(recipeLine.toEntity(), recipeLineEntityFixture())
    }

    @Test
    fun `inventory item round-trips through its entity`() {
        assertEquals(inventoryItem, inventoryItem.toEntity().toDomain())
        assertEquals(inventoryItem.toEntity(), inventoryItemEntityFixture())
    }

    @Test
    fun `stock movement round-trips through its entity`() {
        assertEquals(stockMovement, stockMovement.toEntity().toDomain())
        assertEquals(stockMovement.toEntity(), stockMovementEntityFixture())
    }

    // Explicit entity fixtures: catch a mapper that copies the domain
    // object back verbatim without honoring the column mapping.

    private fun catEntityFixture() = CatEntity(
        id = "cat_1",
        locationId = "loc_1",
        name = "Misu",
        ageMonths = 7,
        sex = "FEMALE",
        photoUri = "content://photos/1",
        status = "ADOPTED",
        intakeDate = "2026-01-02T10:00:00Z",
        notes = "Cariñosa",
        medicalCheck = true,
        tripleVaccine = true,
        sterilized = false,
        createdAt = "2026-01-01T00:00:00Z",
        updatedAt = "2026-01-03T00:00:00Z",
        deletedAt = "2026-01-04T00:00:00Z",
        syncedAt = "2026-01-05T00:00:00Z"
    )

    private fun boardGameEntityFixture() = BoardGameEntity(
        id = "game_1",
        locationId = "loc_1",
        name = "Catan",
        minPlayers = 2,
        maxPlayers = 4,
        durationMinutes = 90,
        difficulty = "HARD",
        status = "MAINTENANCE",
        missingPieces = "1 ficha",
        imageUri = null,
        createdAt = "2026-01-01T00:00:00Z",
        updatedAt = "2026-01-02T00:00:00Z",
        deletedAt = null,
        syncedAt = null
    )

    private fun gameLoanEntityFixture() = GameLoanEntity(
        id = "loan_1",
        locationId = "loc_1",
        boardGameId = "game_1",
        tableReference = "T3",
        startTime = "2026-01-01T10:00:00Z",
        endTime = null,
        createdAt = "2026-01-01T10:00:00Z",
        syncedAt = null
    )

    private fun menuCategoryEntityFixture() = MenuCategoryEntity(
        id = "cat_menu_1",
        locationId = "loc_1",
        name = "Bebidas",
        description = "Cafés y jugos",
        sortOrder = 4,
        createdAt = "2026-01-01T00:00:00Z",
        updatedAt = "2026-01-02T00:00:00Z",
        deletedAt = null,
        syncedAt = "2026-01-03T00:00:00Z"
    )

    private fun menuItemEntityFixture() = MenuItemEntity(
        id = "item_1",
        locationId = "loc_1",
        name = "Latte",
        price = 12.5,
        categoryId = "cat_menu_1",
        description = "Con leche",
        imageUri = "content://images/latte",
        isActive = false,
        createdAt = "2026-01-01T00:00:00Z",
        updatedAt = "2026-01-02T00:00:00Z",
        deletedAt = null,
        syncedAt = null
    )

    private fun recipeLineEntityFixture() = RecipeLineEntity(
        id = "recipe_1",
        menuItemId = "item_1",
        inventoryItemId = "inv_1",
        quantityUsed = 0.018,
        createdAt = "2026-01-01T00:00:00Z",
        updatedAt = "2026-01-01T00:00:00Z"
    )

    private fun inventoryItemEntityFixture() = InventoryItemEntity(
        id = "inv_1",
        locationId = "loc_1",
        name = "Café",
        category = "CAFE",
        baseUnit = "GRAMS",
        maxStock = 5000.0,
        alertThresholdPercent = 25,
        supplierId = "sup_1",
        costPerUnit = 0.08,
        notes = "Tostado medio",
        createdAt = "2026-01-01T00:00:00Z",
        updatedAt = "2026-01-02T00:00:00Z",
        deletedAt = null,
        syncedAt = "2026-01-03T00:00:00Z"
    )

    private fun stockMovementEntityFixture() = StockMovementEntity(
        id = "mov_1",
        locationId = "loc_1",
        inventoryItemId = "inv_1",
        quantity = -2.5,
        type = "REVERSAL",
        referenceOrderId = "order_1",
        note = "Pedido cancelado",
        createdAt = "2026-01-02T00:00:00Z",
        syncedAt = null
    )
}
