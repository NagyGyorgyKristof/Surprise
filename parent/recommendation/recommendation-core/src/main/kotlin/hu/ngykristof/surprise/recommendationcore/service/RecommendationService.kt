package hu.ngykristof.surprise.recommendationcore.service

import hu.ngykristof.surprise.recommendationcore.data.toResult
import hu.ngykristof.surprise.recommendationcore.repository.RecommendationRepository
import hu.ngykristof.surprise.recommendationcore.service.result.PersonalRecommendationResult
import hu.ngykristof.surprise.recommendationcore.service.message.CreateRatingMessage
import org.springframework.stereotype.Service


@Service
class RecommendationService(
        private val recommendationRepository: RecommendationRepository
) {

    fun getUserBasedRecommendation(userId: String): List<PersonalRecommendationResult> {
        return recommendationRepository.userBasedRecommendation(userId).map { it.toResult() }
    }

    fun getContentBasedRecommendation(userId: String): List<PersonalRecommendationResult> {
        return recommendationRepository.contentBasedRecommendation(userId).map { it.toResult() }
    }

    fun getHybridRecommendation(userId: String): List<PersonalRecommendationResult> {
        return recommendationRepository.hybridRecommendation(userId).map { it.toResult() }
    }

    fun createRating(userId: String, createRatingMessage: CreateRatingMessage) {
        recommendationRepository.createRating(userId, createRatingMessage.movieId, createRatingMessage.rating)
    }
}