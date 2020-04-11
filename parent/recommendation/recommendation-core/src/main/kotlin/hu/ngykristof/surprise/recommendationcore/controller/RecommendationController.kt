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


    @GetMapping("/{userId}")
    fun getUserBasedCollaborativeFilterRecommendation(@PathVariable("userId") userId: String): List<PersonalRecommendation> {
        val result = recommendationRepository.userBasedCollaborativeFilterRecommendation(userId)
        return result.map { it.toPersonalRecommendation() }
    }

}
