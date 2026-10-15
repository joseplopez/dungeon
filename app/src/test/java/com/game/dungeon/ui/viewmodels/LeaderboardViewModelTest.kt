package com.game.dungeon.ui.viewmodels

import com.game.dungeon.data.models.GameState
import com.game.dungeon.data.models.LeaderboardEntry
import com.game.dungeon.data.repository.AuthRepository
import com.game.dungeon.data.repository.FriendRepository
import com.game.dungeon.data.repository.GameRepository
import com.game.dungeon.data.repository.LeaderboardRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestWatcher
import org.junit.runner.Description
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever

@OptIn(ExperimentalCoroutinesApi::class)
class LeaderboardMainDispatcherRule : TestWatcher() {
    val testDispatcher: TestDispatcher = UnconfinedTestDispatcher()

    override fun starting(description: Description) {
        Dispatchers.setMain(testDispatcher)
    }

    override fun finished(description: Description) {
        Dispatchers.resetMain()
    }
}

@OptIn(ExperimentalCoroutinesApi::class)
class LeaderboardViewModelTest {

    @get:Rule
    val mainDispatcherRule = LeaderboardMainDispatcherRule()

    private lateinit var gameRepository: GameRepository
    private lateinit var leaderboardRepository: LeaderboardRepository
    private lateinit var authRepository: AuthRepository
    private lateinit var friendRepository: FriendRepository
    private lateinit var viewModel: LeaderboardViewModel

    private val gameStateFlow = MutableStateFlow<GameState?>(null)
    private val partyFlow = MutableStateFlow(emptyList<com.game.dungeon.data.models.Hero>())

    @Before
    fun setUp() = runTest {
        gameRepository = mock()
        leaderboardRepository = mock()
        authRepository = mock()
        friendRepository = mock()

        whenever(gameRepository.getGameState()).thenReturn(gameStateFlow)
        whenever(gameRepository.getParty()).thenReturn(partyFlow)
        whenever(friendRepository.friendIds).thenReturn(MutableStateFlow(emptySet()))

        val globalEntry = LeaderboardEntry(
            rank = 1,
            playerId = "p1",
            playerName = "Player 1",
            maxFloor = 100,
            maxBossFloor = 0,
            totalMagicite = 50,
            fastestClearMs = 1000L
        )
        val bossEntry = LeaderboardEntry(
            rank = 1,
            playerId = "p1",
            playerName = "Player 1",
            maxFloor = 0,
            maxBossFloor = 25,
            totalMagicite = 50,
            fastestClearMs = 1000L
        )

        whenever(leaderboardRepository.getGlobalLeaderboard()).thenReturn(listOf(globalEntry))
        whenever(leaderboardRepository.getBossLairLeaderboard()).thenReturn(listOf(bossEntry))

        viewModel = LeaderboardViewModel(
            repository = gameRepository,
            leaderboardRepository = leaderboardRepository,
            authRepository = authRepository,
            friendRepository = friendRepository
        )
    }

    @Test
    fun `test canSeeBossLair is true when currentDimension is 11 or higher`() = runTest {
        gameStateFlow.value = GameState(currentDimension = 11)
        val canSee = viewModel.canSeeBossLair.first()
        assertTrue(canSee)
    }

    @Test
    fun `test tab 0 fetches global leaderboard and tab 1 fetches boss lair leaderboard`() = runTest {
        gameStateFlow.value = GameState(playerId = "p1", currentDimension = 11)

        // Default tab is 0
        assertEquals(0, viewModel.selectedTab.value)
        val globalEntries = viewModel.leaderboardEntries.first()
        assertEquals(1, globalEntries.size)
        assertEquals(100, globalEntries[0].maxFloor)

        // Select Tab 1 (Boss Lair)
        viewModel.selectTab(1)
        assertEquals(1, viewModel.selectedTab.value)

        val bossEntries = viewModel.leaderboardEntries.first()
        assertEquals(1, bossEntries.size)
        assertEquals(25, bossEntries[0].maxBossFloor)
    }

    @Test
    fun `test user entry appended when not in fetched boss lair entries`() = runTest {
        whenever(leaderboardRepository.getBossLairLeaderboard()).thenReturn(emptyList())

        gameStateFlow.value = GameState(playerId = "p2", playerName = "HeroUser", currentDimension = 11, highestBossLairFloor = 15)

        viewModel.selectTab(1)
        val entries = viewModel.leaderboardEntries.first()
        assertEquals(1, entries.size)
        val userEntry = entries[0]
        assertEquals("p2", userEntry.playerId)
        assertEquals("HeroUser", userEntry.playerName)
        assertEquals(15, userEntry.maxBossFloor)
        assertTrue(userEntry.isUser)
    }
}
