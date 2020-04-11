package hu.ngykristof.surprise.recommendationcore.domain.query

import org.springframework.data.neo4j.annotation.QueryResult

@QueryResult
open class RecommendedMovie() {
    var userId: String = ""
    var title: String = ""
    var ratingMean: Double = 0.0
    var recommendationScore: Double = 0.0
}

