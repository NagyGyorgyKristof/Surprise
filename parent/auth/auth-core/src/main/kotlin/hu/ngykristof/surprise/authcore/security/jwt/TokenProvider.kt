package hu.ngykristof.surprise.authcore.security.jwt

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import hu.ngykristof.surprise.authcore.error.TokenVerificationException
import hu.ngykristof.surprise.authcore.service.messages.RefreshTokenResult
import hu.ngykristof.surprise.commonscore.config.jwt.JwtConfig
import hu.ngykristof.surprise.commonscore.security.SecurityConstants.ROLES_KEY
import hu.ngykristof.surprise.commonscore.security.SecurityConstants.USERNAME_KEY
import hu.ngykristof.surprise.commonscore.util.RandomUtil
import hu.ngykristof.surprise.commonscore.util.logger
import hu.ngykristof.surprise.userapi.dto.loginvalidation.CoreUserInfoResponse
import org.springframework.stereotype.Component
import java.time.OffsetDateTime
import java.util.*

@Component
class TokenProvider(
        private val jwtConfig: JwtConfig
) {

    val logger = logger()


    fun createAccessToken(coreUserInfo: CoreUserInfoResponse): String {
        return JWT.create()
                .withSubject(coreUserInfo.userId)
                .withArrayClaim(ROLES_KEY, coreUserInfo.roles.map { role -> "${jwtConfig.rolePrefix}${role.name}" }.toTypedArray())
                .withClaim(USERNAME_KEY, coreUserInfo.username)
                .withExpiresAt(Date.from(OffsetDateTime.now().plusMinutes(jwtConfig.accessTokenValidityInMins.toLong()).toInstant()))
                .sign(Algorithm.HMAC512(jwtConfig.secret.toByteArray()))
    }

    fun createRefreshToken(coreUserInfo: CoreUserInfoResponse): RefreshTokenResult {
        return RefreshTokenResult(
                value = RandomUtil.generateRefreshToken(),
                userId = coreUserInfo.userId,
                expirationDate = OffsetDateTime.now().plusDays(jwtConfig.refreshTokenValidityInDays.toLong())
        )
    }

    fun verifyAccessToken(accessToken: String) {
        //Throw an JWTDecodeException exception if the token is not formatted or it has been expired
        JWT.require(Algorithm.HMAC512(jwtConfig.secret.toByteArray()))
                .build()
                .verify(accessToken.replace(jwtConfig.tokenPrefix, ""))
    }


    fun verifyRefreshToken(refreshTokenExpirationDate: OffsetDateTime) {
        if (OffsetDateTime.now().isAfter(refreshTokenExpirationDate)) {
            logger.info("Invalid RefreshToken exception has been occurred with message:")
            throw TokenVerificationException("RefreshToken has been expired")
        }
    }
}
