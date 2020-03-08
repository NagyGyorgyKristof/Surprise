package hu.ngykristof.surprise.authcore.service

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import hu.ngykristof.surprise.authapi.dto.login.LoginRequest
import hu.ngykristof.surprise.authcore.error.UserLoginFailedException
import hu.ngykristof.surprise.commomconfig.config.jwt.JwtConfig
import hu.ngykristof.surprise.userapi.UserFeignClient
import hu.ngykristof.surprise.userapi.dto.loginvalidation.ValidateUserLoginRequest
import org.springframework.stereotype.Service
import java.util.*

@Service
class LoginService(
        private val jwtConfig: JwtConfig,
        private val userFeignClient: UserFeignClient
) {

    fun loginUser(loginRequest: LoginRequest): String = try {

        val validationResponse = userFeignClient.validateLogin(ValidateUserLoginRequest(
                username = loginRequest.username,
                password = loginRequest.password)
        )

        JWT.create()
                .withSubject(validationResponse.userId)
                .withArrayClaim("roles", validationResponse.roles.map { "${jwtConfig.rolePrefix}${it.name}" }.toTypedArray())
                .withClaim("username", validationResponse.username)
                .withExpiresAt(Date(System.currentTimeMillis() + jwtConfig.expiration.toInt()))
                .sign(Algorithm.HMAC512(jwtConfig.secret.toByteArray()))

    } catch (e: Exception) {
        throw UserLoginFailedException()
    }
}