package com.mishin.feature.games

import app.cash.turbine.TurbineTestContext
import app.cash.turbine.test
import com.mishin.core.common.constants.Defaults
import com.mishin.core.domain.model.GameDifficulty
import com.mishin.core.domain.model.GameStatus
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class GamesViewModelTest {

    private lateinit var gameRepository: FakeGameRepository
    private lateinit var settingsRepository: FakeSettingsRepository

    @Before
    fun setUp() {
        Dispatchers.setMain(UnconfinedTestDispatcher())
        gameRepository = FakeGameRepository()
        settingsRepository = FakeSettingsRepository()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun viewModel() = GamesViewModel(gameRepository, settingsRepository)

    /** Reads items until [predicate] holds, so emission order never matters. */
    private suspend fun <T> TurbineTestContext<T>.awaitUntil(predicate: (T) -> Boolean): T {
        while (true) {
            val item = awaitItem()
            if (predicate(item)) return item
        }
    }

    // -- Settings state --

    @Test
    fun `settings flows expose the documented defaults before anything is saved`() {
        val viewModel = viewModel()

        assertEquals("3", viewModel.karaokePrice.value)
        assertEquals("30", viewModel.karaokePeriodMinutes.value)
        assertEquals(
            "Canta tus canciones favoritas con amigos, sin importar el tono.",
            viewModel.karaokeDescription.value
        )
        assertEquals(
            "Si lo armas en menos de una hora, tu consumo es gratis (hasta de 4 personas).",
            viewModel.gamesPromo.value
        )
    }

    @Test
    fun `settings flows show values persisted by an earlier session`() = runTest {
        settingsRepository.seed(GamesViewModel.KARAOKE_PRICE_KEY, "7")
        settingsRepository.seed(GamesViewModel.GAMES_PROMO_KEY, "Promo guardada")

        val viewModel = viewModel()

        assertEquals("7", viewModel.karaokePrice.first { it == "7" })
        assertEquals("Promo guardada", viewModel.gamesPromo.first { it == "Promo guardada" })
        // Keys never written still fall back to their defaults.
        assertEquals("30", viewModel.karaokePeriodMinutes.first())
    }

    @Test
    fun `settings flows update after the sheet is saved`() = runTest {
        val viewModel = viewModel()

        viewModel.saveKaraoke(
            price = "5",
            periodMinutes = "15",
            description = "Sábado con karaoke"
        )
        viewModel.savePromo("2x1 en juegos de mesa")

        assertEquals("5", viewModel.karaokePrice.first { it == "5" })
        assertEquals("15", viewModel.karaokePeriodMinutes.first { it == "15" })
        assertEquals(
            "Sábado con karaoke",
            viewModel.karaokeDescription.first { it == "Sábado con karaoke" }
        )
        assertEquals(
            "2x1 en juegos de mesa",
            viewModel.gamesPromo.first { it == "2x1 en juegos de mesa" }
        )
    }

    // -- Karaoke / promo writes --

    @Test
    fun `saveKaraoke persists normalized values`() = runTest {
        val viewModel = viewModel()

        viewModel.saveKaraoke(
            price = " 5,5 ",
            periodMinutes = " 15 ",
            description = "  Canta con amigos  "
        )

        assertEquals(
            listOf(
                GamesViewModel.KARAOKE_PRICE_KEY to "5.5",
                GamesViewModel.KARAOKE_PERIOD_KEY to "15",
                GamesViewModel.KARAOKE_DESCRIPTION_KEY to "Canta con amigos"
            ),
            settingsRepository.writes
        )
    }

    @Test
    fun `saveKaraoke writes whole number prices without decimals`() = runTest {
        val viewModel = viewModel()

        viewModel.saveKaraoke(price = "5.0", periodMinutes = "30", description = "Descripción")

        assertEquals("5", settingsRepository.writes.first().second)
    }

    @Test
    fun `saveKaraoke ignores invalid input`() = runTest {
        val viewModel = viewModel()

        viewModel.saveKaraoke(price = "abc", periodMinutes = "15", description = "ok")
        viewModel.saveKaraoke(price = "-2", periodMinutes = "15", description = "ok")
        viewModel.saveKaraoke(price = "5", periodMinutes = "0", description = "ok")
        viewModel.saveKaraoke(price = "5", periodMinutes = "x", description = "ok")
        viewModel.saveKaraoke(price = "5", periodMinutes = "15", description = "   ")

        assertTrue(settingsRepository.writes.isEmpty())
    }

    @Test
    fun `savePromo trims the text before persisting it`() = runTest {
        val viewModel = viewModel()

        viewModel.savePromo("  Oferta 2x1  ")

        assertEquals(
            listOf(GamesViewModel.GAMES_PROMO_KEY to "Oferta 2x1"),
            settingsRepository.writes
        )
    }

    @Test
    fun `savePromo ignores blank text`() = runTest {
        val viewModel = viewModel()

        viewModel.savePromo("   ")

        assertTrue(settingsRepository.writes.isEmpty())
    }

    // -- Games CRUD --

    @Test
    fun `games flow mirrors the repository`() = runTest {
        gameRepository.seed(boardGame(name = "Catán"))
        val viewModel = viewModel()

        viewModel.games.test {
            val games = awaitUntil { it.isNotEmpty() }
            assertEquals(listOf("Catán"), games.map { it.name })

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `saveGame creates a new game with a trimmed name`() = runTest {
        val viewModel = viewModel()

        viewModel.saveGame(
            existing = null,
            name = "  Catanes  ",
            minPlayers = 2,
            maxPlayers = 6,
            durationMinutes = 45,
            difficulty = GameDifficulty.EASY,
            status = GameStatus.AVAILABLE,
            missingPieces = "1 ficha rota",
            imageUri = "content://photos/1"
        )

        val saved = gameRepository.upserted.single()
        assertEquals("Catanes", saved.name)
        assertEquals(Defaults.DEFAULT_LOCATION_ID, saved.locationId)
        assertTrue(saved.id.isNotBlank())
        assertTrue(saved.createdAt.isNotBlank())
        assertEquals(saved.createdAt, saved.updatedAt)
        assertNull(saved.deletedAt)
        assertNull(saved.syncedAt)
        assertEquals("1 ficha rota", saved.missingPieces)
        assertEquals("content://photos/1", saved.imageUri)
    }

    @Test
    fun `saved games show up in the games flow`() = runTest {
        val viewModel = viewModel()

        viewModel.saveGame(
            existing = null,
            name = "Juego Foto",
            minPlayers = 2,
            maxPlayers = 4,
            durationMinutes = 30,
            difficulty = GameDifficulty.MEDIUM,
            status = GameStatus.AVAILABLE,
            missingPieces = null,
            imageUri = null
        )

        viewModel.games.test {
            val games = awaitUntil { it.isNotEmpty() }
            assertEquals(listOf("Juego Foto"), games.map { it.name })

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `saveGame updates an existing game keeping its identity`() = runTest {
        val existing = boardGame(id = "game_42", name = "Catán")
        val viewModel = viewModel()

        viewModel.saveGame(
            existing = existing,
            name = "Catan Editado",
            minPlayers = 2,
            maxPlayers = 4,
            durationMinutes = 45,
            difficulty = GameDifficulty.HARD,
            status = GameStatus.MAINTENANCE,
            missingPieces = null,
            imageUri = "content://photos/2"
        )

        val saved = gameRepository.upserted.single()
        assertEquals(existing.id, saved.id)
        assertEquals(existing.createdAt, saved.createdAt)
        assertEquals(existing.locationId, saved.locationId)
        assertEquals("Catan Editado", saved.name)
        assertEquals(45, saved.durationMinutes)
        assertEquals(GameDifficulty.HARD, saved.difficulty)
        assertEquals(GameStatus.MAINTENANCE, saved.status)
        assertEquals("content://photos/2", saved.imageUri)
    }

    @Test
    fun `saveGame ignores invalid input`() = runTest {
        val viewModel = viewModel()

        fun attempt(name: String, min: Int, max: Int, duration: Int) {
            viewModel.saveGame(
                existing = null,
                name = name,
                minPlayers = min,
                maxPlayers = max,
                durationMinutes = duration,
                difficulty = GameDifficulty.MEDIUM,
                status = GameStatus.AVAILABLE,
                missingPieces = null,
                imageUri = null
            )
        }

        attempt(name = "   ", min = 2, max = 4, duration = 45) // blank name
        attempt(name = "Nombre", min = 0, max = 4, duration = 45) // no players
        attempt(name = "Nombre", min = 3, max = 2, duration = 45) // max < min
        attempt(name = "Nombre", min = 2, max = 4, duration = 0) // no duration

        assertTrue(gameRepository.upserted.isEmpty())
    }

    @Test
    fun `deleteGame removes the game from the repository`() = runTest {
        gameRepository.seed(boardGame(id = "game_9"))
        val viewModel = viewModel()

        viewModel.deleteGame("game_9")

        assertEquals(listOf("game_9"), gameRepository.deletedIds)
        assertNull(gameRepository.getGameById("game_9"))
        assertTrue(viewModel.games.first().isEmpty())
    }
}
