package hu.ngykristof.surprise.recommendationcore.controller

import hu.ngykristof.surprise.recommendationapi.dto.PersonalRecommendation
import hu.ngykristof.surprise.recommendationcore.controller.mapping.toPersonalRecommendation
import hu.ngykristof.surprise.recommendationcore.repository.RecommendationRepository
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController


@RestController
class RecommendationController(
        private val recommendationRepository: RecommendationRepository
) {


    @GetMapping("/user-based/{userId}")
    fun getUserBasedRecommendation(@PathVariable("userId") userId: String): List<PersonalRecommendation> {
        val result = recommendationRepository.userBasedRecommendation(userId)
        return result.map { it.toPersonalRecommendation() }
    }

    @GetMapping("/content-based/{userId}")
    fun getContentBasedRecommendation(@PathVariable("userId") userId: String): List<PersonalRecommendation> {
        val result = recommendationRepository.contentBasedRecommendation(userId)
        return result.map { it.toPersonalRecommendation() }
    }

    @GetMapping("/hybrid/{userId}")
    fun getHybridRecommendation(@PathVariable("userId") userId: String): List<PersonalRecommendation> {
        val result = recommendationRepository.hybridRecommendation(userId)
        return result.map { it.toPersonalRecommendation() }
    }

}
