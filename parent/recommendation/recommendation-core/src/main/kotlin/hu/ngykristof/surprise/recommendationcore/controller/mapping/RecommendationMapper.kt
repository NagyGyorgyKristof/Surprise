package hu.ngykristof.surprise.recommendationcore.controller.mapping

import hu.ngykristof.surprise.recommendationapi.dto.PersonalRecommendationResponse
import hu.ngykristof.surprise.recommendationcore.data.PersonalRecommendationEntity

fun PersonalRecommendationEntity.toPersonalRecommendation(): PersonalRecommendationResponse {
    return PersonalRecommendationResponse(
            title = this.title,
            ratingMean = this.ratingMean,
            movieId = this.movieId
    )
}
