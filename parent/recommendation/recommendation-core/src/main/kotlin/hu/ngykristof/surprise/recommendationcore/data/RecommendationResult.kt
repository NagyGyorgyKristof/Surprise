package hu.ngykristof.surprise.recommendationcore.data

import org.springframework.data.neo4j.annotation.QueryResult

@QueryResult
class RecommendationResult {
    var movieId: Int = 0
    var title: String = ""
    var ratingMean: Double = 0.0
}