package hu.ngykristof.surprise.recommendationcore.service.message

import hu.ngykristof.surprise.recommendationapi.dto.CreateRatingRequest

class CreateRatingMessage(
        val movieId: String,
        val rating: Double
)

fun CreateRatingRequest.toMessage(): CreateRatingMessage {
    return CreateRatingMessage(
            movieId = this.movieId,
            rating = this.rating
    )
}
