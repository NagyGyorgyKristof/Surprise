package hu.ngykristof.surprise.authcore.repository

import hu.ngykristof.surprise.authcore.domain.RefreshTokenEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.time.OffsetDateTime


@Repository
interface TokenRepository : JpaRepository<RefreshTokenEntity, String> {

    fun findByValue(value: String): RefreshTokenEntity?

    fun findOneByUserId(value: String): RefreshTokenEntity?

    fun findAllByExpirationDateBefore(now: OffsetDateTime): List<RefreshTokenEntity>
}
