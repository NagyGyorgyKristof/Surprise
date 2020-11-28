package hu.ngykristof.surprise.recommendationcore.service

import hu.ngykristof.surprise.recommendationcore.repository.GeneralGraphRepository
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

internal class GeneralGraphServiceTest {

    private lateinit var generalGraphRepository: GeneralGraphRepository
    private lateinit var generalGraphService: GeneralGraphService

    @BeforeEach
    fun setUp() {
        this.generalGraphRepository = mockk(relaxed = true)
        generalGraphService = GeneralGraphService(generalGraphRepository)
    }

    @Test
    fun isGraphEmpty_WhenGraphExists() {
        val nodeCount = 1

        every { generalGraphRepository.getNodeCount() } returns nodeCount

        val graphEmpty = generalGraphService.isGraphEmpty()

        assertFalse(graphEmpty)
    }

    @Test
    fun isGraphEmpty_WhenGraphIsEmpty() {
        val nodeCount = 0

        every { generalGraphRepository.getNodeCount() } returns nodeCount

        val graphEmpty = generalGraphService.isGraphEmpty()

        assertTrue(graphEmpty)
    }
}