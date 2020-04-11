package hu.ngykristof.surprise.recommendationapi.dto

class PersonalRecommendation(
        var userId: String = "",
        var title: String = "",
        var ratingMean: Double = 0.0,
        var recommendationScore: Double = 0.0
)
