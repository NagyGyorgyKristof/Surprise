package hu.ngykristof.surprise.authcore.security

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.fasterxml.jackson.databind.ObjectMapper
import hu.ngykristof.surprise.commonscore.config.jwt.JwtConfig
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.userdetails.User
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import java.util.*
import javax.servlet.FilterChain
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

class JwtAuthenticationFilter(
        private val authManager: AuthenticationManager,
        private val jwtConfig: JwtConfig
) : UsernamePasswordAuthenticationFilter() {
    override fun attemptAuthentication(request: HttpServletRequest, response: HttpServletResponse): Authentication {
        val user = ObjectMapper().readValue(request.inputStream, UserCredentials::class.java)

        return authManager.authenticate(
                UsernamePasswordAuthenticationToken(
                        user.username,
                        user.password,
                        emptyList()
                )
        )
    }

    override fun successfulAuthentication(request: HttpServletRequest, response: HttpServletResponse, chain: FilterChain, authResult: Authentication) {
        val token = JWT.create()
                .withSubject((authResult.principal as User).username)
                .withArrayClaim("roles", authResult.authorities.map { it.authority }.toTypedArray())
                .withExpiresAt(Date(System.currentTimeMillis() + jwtConfig.expiration.toInt()))
                .sign(Algorithm.HMAC512(jwtConfig.secret.toByteArray()))

        response.addHeader(jwtConfig.header, "${jwtConfig.tokenPrefix}$token")
    }

    data class UserCredentials(
            var username: String = "",
            var password: String = ""
    )
}