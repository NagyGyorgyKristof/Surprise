package hu.ngykristof.surprise.recommendationapi

import hu.ngykristof.surprise.recommendationapi.dto.PersonalRecommendation
import org.springframework.cloud.openfeign.FeignClient
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable

@FeignClient("recommendation-service", decode404 = true)
interface RecommendationFeignClient {

    @GetMapping("/recommendation/{userId}")
    fun getUserBasedCollaborativeFilterRecommendation(@PathVariable("userId") userId: String): List<PersonalRecommendation>

}
