package hu.ngykristof.surprise.recommendationcore.data

import hu.ngykristof.surprise.recommendationcore.service.result.PersonalRecommendationResult
import org.springframework.data.neo4j.annotation.QueryResult

@QueryResult
class PersonalRecommendationEntity {
    var movieId: Int = 0
    var title: String = ""
    var ratingMean: Double = 0.0
}

fun PersonalRecommendationEntity.toResult(): PersonalRecommendationResult {
    return PersonalRecommendationResult(
            title = this.title,
            ratingMean = this.ratingMean,
            movieId = this.movieId
    )
}