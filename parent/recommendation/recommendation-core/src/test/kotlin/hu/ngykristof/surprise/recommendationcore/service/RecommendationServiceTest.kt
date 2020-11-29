package hu.ngykristof.surprise.recommendationcore.service

import hu.ngykristof.surprise.recommendationapi.dto.CreateRatingRequest
import hu.ngykristof.surprise.recommendationcore.data.PersonalRecommendationEntity
import hu.ngykristof.surprise.recommendationcore.repository.RecommendationRepository
import hu.ngykristof.surprise.recommendationcore.service.message.CreateRatingMessage
import hu.ngykristof.surprise.recommendationcore.service.message.toMessage
import io.mockk.confirmVerified
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

internal class RecommendationServiceTest {

    private lateinit var recommendationRepository: RecommendationRepository
    private lateinit var recommendationService: RecommendationService

    @BeforeEach
    fun setUp() {
        this.recommendationRepository = mockk(relaxed = true)
        this.recommendationService = RecommendationService(recommendationRepository)
    }

    @Test
    fun getUserBasedRecommendation() {
        val userId = "userId"
        val recommendationEntity = PersonalRecommendationEntity()

        every { recommendationRepository.userBasedRecommendation(userId) } returns listOf(recommendationEntity)

        val userBasedRecommendation = recommendationService.getUserBasedRecommendation(userId)

        verify { recommendationRepository.userBasedRecommendation(userId) }
        confirmVerified(recommendationRepository)

        assertEquals(userBasedRecommendation.first().movieId, recommendationEntity.movieId)
        assertEquals(userBasedRecommendation.first().ratingMean, recommendationEntity.ratingMean)
        assertEquals(userBasedRecommendation.first().title, recommendationEntity.title)
    }

    @Test
    fun getContentBasedRecommendation() {
        val userId = "userId"
        val recommendationEntity = PersonalRecommendationEntity()

        every { recommendationRepository.contentBasedRecommendation(userId) } returns listOf(recommendationEntity)

        val userBasedRecommendation = recommendationService.getContentBasedRecommendation(userId)

        verify { recommendationRepository.contentBasedRecommendation(userId) }
        confirmVerified(recommendationRepository)

        assertEquals(userBasedRecommendation.first().movieId, recommendationEntity.movieId)
        assertEquals(userBasedRecommendation.first().ratingMean, recommendationEntity.ratingMean)
        assertEquals(userBasedRecommendation.first().title, recommendationEntity.title)
    }

    @Test
    fun getHybridRecommendation() {
        val userId = "userId"
        val recommendationEntity = PersonalRecommendationEntity()

        every { recommendationRepository.hybridRecommendation(userId) } returns listOf(recommendationEntity)

        val userBasedRecommendation = recommendationService.getHybridRecommendation(userId)

        verify { recommendationRepository.hybridRecommendation(userId) }
        confirmVerified(recommendationRepository)

        assertEquals(userBasedRecommendation.first().movieId, recommendationEntity.movieId)
        assertEquals(userBasedRecommendation.first().ratingMean, recommendationEntity.ratingMean)
        assertEquals(userBasedRecommendation.first().title, recommendationEntity.title)
    }

    @Test
    fun createRating() {
        val userId = "userId"
        val movieId = "movieId"
        val rating = 5.0
        val createRatingMessage = CreateRatingMessage(movieId, rating)

        recommendationService.createRating(userId, createRatingMessage)

        verify { recommendationRepository.createRating(userId, movieId, rating) }
        confirmVerified(recommendationRepository)
    }

    @Test
    fun createRatingMapping() {
        val userId = "userId"
        val movieId = "movieId"
        val rating = 5.0
        val createRatingMessage = CreateRatingRequest(movieId, rating)

        recommendationService.createRating(userId, createRatingMessage.toMessage())

        verify { recommendationRepository.createRating(userId, movieId, rating) }
        confirmVerified(recommendationRepository)
    }
}