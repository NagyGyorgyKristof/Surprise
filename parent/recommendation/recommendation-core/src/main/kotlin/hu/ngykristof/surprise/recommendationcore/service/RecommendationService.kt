package hu.ngykristof.surprise.recommendationcore.service

import hu.ngykristof.surprise.recommendationapi.dto.CreateRatingRequest
import hu.ngykristof.surprise.recommendationcore.controller.mapping.toPersonalRecommendation
import hu.ngykristof.surprise.recommendationcore.data.toResult
import hu.ngykristof.surprise.recommendationcore.repository.RecommendationRepository
import hu.ngykristof.surprise.recommendationcore.service.result.PersonalRecommendationResult
import hu.ngykristof.surprise.recommendationcore.service.result.message.CreateRatingMessage
import org.springframework.stereotype.Service
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestBody


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