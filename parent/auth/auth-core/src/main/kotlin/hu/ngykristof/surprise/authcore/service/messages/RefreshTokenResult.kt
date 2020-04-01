package hu.ngykristof.surprise.authcore.service.messages

import java.time.OffsetDateTime

class RefreshTokenResult(
        val userId: String,
        val expirationDate: OffsetDateTime,
        val value: String
)
