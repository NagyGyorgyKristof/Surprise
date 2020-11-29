package hu.ngykristof.surprise.recommendationapi

import hu.ngykristof.surprise.recommendationapi.dto.CreateRatingRequest
import hu.ngykristof.surprise.recommendationapi.dto.PersonalRecommendationResponse
import org.springframework.cloud.openfeign.FeignClient
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody

@FeignClient("recommendation-service", decode404 = true)
interface RecommendationFeignClient {

    @GetMapping("/recommendation/user-based/{userId}")
    fun getUserBasedRecommendation(@PathVariable("userId") userId: String): List<PersonalRecommendationResponse>

    @GetMapping("/recommendation/content-based/{userId}")
    fun getContentBasedRecommendation(@PathVariable("userId") userId: String): List<PersonalRecommendationResponse>

    @GetMapping("/hybrid/{userId}")
    fun getHybridRecommendation(@PathVariable("userId") userId: String): List<PersonalRecommendationResponse>

    @PostMapping("/ratings/{userId}")
    fun createRating(@PathVariable("userId") userId: String, @RequestBody createRatingRequest: CreateRatingRequest)
}
