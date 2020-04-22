package hu.ngykristof.surprise.recommendationcore.data.query

import org.springframework.data.neo4j.annotation.QueryResult

@QueryResult
open class UserBasedRecommendation() {
    var userId: String = ""
    var title: String = ""
    var ratingMean: Double = 0.0
    var recommendationScore: Double = 0.0
}

