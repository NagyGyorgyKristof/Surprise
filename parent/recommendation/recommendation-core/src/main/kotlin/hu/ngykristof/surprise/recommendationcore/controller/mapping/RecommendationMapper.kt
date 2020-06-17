package hu.ngykristof.surprise.recommendationcore.controller.mapping

import hu.ngykristof.surprise.recommendationapi.dto.PersonalRecommendation
import hu.ngykristof.surprise.recommendationcore.data.RecommendationResult

fun RecommendationResult.toPersonalRecommendation(): PersonalRecommendation {
    return PersonalRecommendation(
            title = this.title,
            ratingMean = this.ratingMean,
            movieId = this.movieId
    )
}
