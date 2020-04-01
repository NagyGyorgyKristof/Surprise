package hu.ngykristof.surprise.authcore.mapper

import hu.ngykristof.surprise.authcore.domain.RefreshTokenEntity
import hu.ngykristof.surprise.authcore.service.messages.RefreshTokenResult

fun RefreshTokenResult.toRefreshTokenEntity(): RefreshTokenEntity {
    return RefreshTokenEntity(
            userId = this.userId,
            expirationDate = this.expirationDate,
            value = this.value
    )
}
