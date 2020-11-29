package hu.ngykristof.surprise.recommendationcore.service.result

import hu.ngykristof.surprise.recommendationapi.dto.PersonalRecommendation

class PersonalRecommendationResult(
        var title: String = "",
        var ratingMean: Double = 0.0,
        var movieId: Int = 0
)

fun PersonalRecommendationResult.toResponse(): PersonalRecommendation {
    return PersonalRecommendation(
            title = this.title,
            ratingMean = this.ratingMean,
            movieId = this.movieId
    )
}
