package hu.ngykristof.surprise.recommendationcore.data

import hu.ngykristof.surprise.recommendationcore.service.result.PersonalRecommendationResult
import hu.ngykristof.surprise.recommendationcore.service.result.toResponse
import org.springframework.data.neo4j.annotation.QueryResult

@QueryResult
class RecommendationResult {
    var movieId: Int = 0
    var title: String = ""
    var ratingMean: Double = 0.0
}

fun RecommendationResult.toResult(): PersonalRecommendationResult {
    return PersonalRecommendationResult(
            title = this.title,
            ratingMean = this.ratingMean,
            movieId = this.movieId
    )
}