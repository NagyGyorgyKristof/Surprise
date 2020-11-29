package hu.ngykristof.surprise.recommendationcore.controller

import hu.ngykristof.surprise.recommendationapi.dto.PersonalRecommendationResponse
import hu.ngykristof.surprise.recommendationapi.dto.CreateRatingRequest
import hu.ngykristof.surprise.recommendationcore.controller.mapping.toPersonalRecommendation
import hu.ngykristof.surprise.recommendationcore.repository.RecommendationRepository
import org.springframework.web.bind.annotation.*


@RestController
class RecommendationController(
        private val recommendationRepository: RecommendationRepository
) {


    @GetMapping("/user-based/{userId}")
    fun getUserBasedRecommendation(@PathVariable("userId") userId: String): List<PersonalRecommendationResponse> {
        val result = recommendationRepository.userBasedRecommendation(userId)
        return result.map { it.toPersonalRecommendation() }
    }

    @GetMapping("/content-based/{userId}")
    fun getContentBasedRecommendation(@PathVariable("userId") userId: String): List<PersonalRecommendationResponse> {
        val result = recommendationRepository.contentBasedRecommendation(userId)
        return result.map { it.toPersonalRecommendation() }
    }

    @GetMapping("/hybrid/{userId}")
    fun getHybridRecommendation(@PathVariable("userId") userId: String): List<PersonalRecommendationResponse> {
        val result = recommendationRepository.hybridRecommendation(userId)
        return result.map { it.toPersonalRecommendation() }
    }

    @PostMapping("/ratings/{userId}")
    fun createRating(@PathVariable("userId") userId: String, @RequestBody createRatingRequest: CreateRatingRequest) {
        recommendationRepository.createRating(
                userId = userId,
                movieId = createRatingRequest.movieId,
                rating = createRatingRequest.rating
        )
    }
}
