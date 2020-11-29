package hu.ngykristof.surprise.recommendationcore.test

import hu.ngykristof.surprise.recommendationcore.data.PersonalRecommendationEntity
import hu.ngykristof.surprise.recommendationcore.service.message.CreateRatingMessage

fun createMockPersonalRecommendationEntity() = PersonalRecommendationEntity()

fun createMockCreateRatingMessage(movieId: String, rating: Double) =
        CreateRatingMessage(movieId, rating)