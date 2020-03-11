package hu.ngykristof.surprise.authcore.service

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.auth0.jwt.exceptions.JWTVerificationException
import hu.ngykristof.surprise.authapi.dto.tokenvalidation.TokenValidationRequest
import hu.ngykristof.surprise.authapi.dto.tokenvalidation.TokenValidationResponse
import hu.ngykristof.surprise.commonscore.config.jwt.JwtConfig
import org.springframework.stereotype.Service


@Service
class TokenService(
        private val jwtConfig: JwtConfig
) {

    fun validateToken(validateTokenRequest: TokenValidationRequest): TokenValidationResponse {
        return try {
            val accessToken = validateTokenRequest.accessToken

            //Throw an JWTDecodeException exception if the token is not formatted or it has been expired
            JWT.require(Algorithm.HMAC512(jwtConfig.secret.toByteArray()))
                    .build()
                    .verify(accessToken.replace(jwtConfig.tokenPrefix, ""))

            TokenValidationResponse(isValid = true, accessToken = accessToken)
        } catch (e: JWTVerificationException) {
            TokenValidationResponse(isValid = false)
        }
    }
}