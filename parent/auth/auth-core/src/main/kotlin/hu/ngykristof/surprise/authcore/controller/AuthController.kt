package hu.ngykristof.surprise.authcore.controller

import hu.ngykristof.surprise.authapi.dto.login.LoginRequest
import hu.ngykristof.surprise.authapi.dto.login.LoginResponse
import hu.ngykristof.surprise.authapi.dto.tokenvalidation.TokenValidationRequest
import hu.ngykristof.surprise.authapi.dto.tokenvalidation.TokenValidationResponse
import hu.ngykristof.surprise.authcore.service.LoginService
import hu.ngykristof.surprise.authcore.service.TokenService
import hu.ngykristof.surprise.commonscore.config.jwt.JwtConfig
import org.springframework.http.HttpHeaders
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
class AuthController(
        private val tokenService: TokenService,
        private val loginService: LoginService,
        private val jwtConfig: JwtConfig
) {


    @PostMapping("/validate")
    fun validateToken(@RequestBody validateTokenRequest: TokenValidationRequest): TokenValidationResponse {
        return tokenService.validateToken(validateTokenRequest)
    }

    @PostMapping("login")
    fun login(@RequestBody loginRequest: LoginRequest): ResponseEntity<LoginResponse> {
        val accessToken = loginService.loginUser(loginRequest)
        val responseHeaders = HttpHeaders()
        responseHeaders.set(jwtConfig.header, "${jwtConfig.tokenPrefix}$accessToken")

        return ResponseEntity.ok().headers(responseHeaders).build()
    }


}