package hu.ngykristof.surprise.recommendationcore.controller

import hu.ngykristof.surprise.recommendationapi.dto.PersonalRecommendationResponse
import hu.ngykristof.surprise.recommendationapi.dto.CreateRatingRequest
import hu.ngykristof.surprise.recommendationcore.service.RecommendationService
import hu.ngykristof.surprise.recommendationcore.service.message.toMessage
import hu.ngykristof.surprise.recommendationcore.service.result.toResponse
import org.springframework.web.bind.annotation.*


@RestController
class RecommendationController(
        private val recommendationService: RecommendationService
) {


    @GetMapping("/user-based/{userId}")
    fun getUserBasedRecommendation(@PathVariable("userId") userId: String): List<PersonalRecommendationResponse> {
        return recommendationService.getUserBasedRecommendation(userId).map { it.toResponse() }
    }

    @GetMapping("/content-based/{userId}")
    fun getContentBasedRecommendation(@PathVariable("userId") userId: String): List<PersonalRecommendationResponse> {
        return recommendationService.getContentBasedRecommendation(userId).map { it.toResponse() }
    }

    @GetMapping("/hybrid/{userId}")
    fun getHybridRecommendation(@PathVariable("userId") userId: String): List<PersonalRecommendationResponse> {
        return recommendationService.getHybridRecommendation(userId).map { it.toResponse() }
    }

    @PostMapping("/ratings/{userId}")
    fun createRating(@PathVariable("userId") userId: String, @RequestBody createRatingRequest: CreateRatingRequest) {
        recommendationService.createRating(userId, createRatingRequest.toMessage())
    }
}
