package hu.ngykristof.surprise.recommendationcore.controller.mapping

import hu.ngykristof.surprise.recommendationapi.dto.PersonalRecommendation
import hu.ngykristof.surprise.recommendationcore.domain.query.RecommendedMovie

fun RecommendedMovie.toPersonalRecommendation(): PersonalRecommendation {
    return PersonalRecommendation(
            userId = this.userId,
            title = this.title,
            ratingMean = this.ratingMean,
            recommendationScore = this.recommendationScore
    )
}
