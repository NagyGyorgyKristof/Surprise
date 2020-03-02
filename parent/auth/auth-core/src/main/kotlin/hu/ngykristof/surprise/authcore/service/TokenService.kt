package hu.ngykristof.surprise.authcore.service

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.auth0.jwt.exceptions.JWTDecodeException
import hu.ngykristof.surprise.authapi.dto.tokenvalidation.ValidateTokenRequest
import hu.ngykristof.surprise.authapi.dto.tokenvalidation.ValidateTokenResponse
import hu.ngykristof.surprise.commonscore.config.jwt.JwtConfig
import org.springframework.stereotype.Service


@Service
class TokenService(
        private val jwtConfig: JwtConfig
) {

    fun validateToken(validateTokenRequest: ValidateTokenRequest): ValidateTokenResponse {
        return try {
            //Throw an JWTDecodeException exception if the token is not formatted or it has been expired
            JWT.require(Algorithm.HMAC512(jwtConfig.secret.toByteArray()))
                    .build()
                    .verify(validateTokenRequest
                            .accessToken
                            .replace(jwtConfig.tokenPrefix, "")
                    )

            ValidateTokenResponse(isValid = true)
        } catch (e: JWTDecodeException) {
            ValidateTokenResponse(isValid = false)
        }
    }
}