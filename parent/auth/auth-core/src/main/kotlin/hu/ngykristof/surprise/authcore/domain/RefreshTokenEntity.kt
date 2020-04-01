package hu.ngykristof.surprise.authcore.domain

import hu.ngykristof.surprise.commonscore.domain.BaseEntity
import java.time.OffsetDateTime
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Table

@Entity
@Table(name = "refresh_tokens")
class RefreshTokenEntity(
        @Column(unique = true, nullable = false)
        var userId: String,
        val expirationDate: OffsetDateTime,
        var value: String
) : BaseEntity()
