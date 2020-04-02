package hu.ngykristof.surprise.authcore.controller.internal

import hu.ngykristof.surprise.authapi.dto.login.LoginRequest
import hu.ngykristof.surprise.authapi.dto.login.TokenResponse
import hu.ngykristof.surprise.authapi.dto.renewtoken.AccessTokenRenewalRequest
import hu.ngykristof.surprise.authcore.service.AuthService
import hu.ngykristof.surprise.authcore.service.messages.TokenResult
import hu.ngykristof.surprise.commonscore.config.jwt.JwtConfig
import hu.ngykristof.surprise.commonscore.config.jwt.WithUserInfo
import hu.ngykristof.surprise.commonscore.dto.UserInfo
import org.springframework.http.HttpHeaders
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
class AuthController(
        private val authService: AuthService,
        private val jwtConfig: JwtConfig
) {

    @PostMapping("/authenticate")
    fun login(@RequestBody loginRequest: LoginRequest): ResponseEntity<TokenResponse> {
        val tokenResult = authService.loginUser(loginRequest)
        val authorizationHeader = createAuthorizationHeader(tokenResult)

        return ResponseEntity
                .ok()
                .headers(authorizationHeader)
                .body(TokenResponse(refreshToken = tokenResult.refreshTokenValue))

    }

    @PostMapping("/renew-access-token")
    fun renewAccessToken(@RequestBody renewalRequest: AccessTokenRenewalRequest): ResponseEntity<TokenResponse> {
        val tokenResult = authService.renewToken(renewalRequest)
        val responseHeaders = createAuthorizationHeader(tokenResult)

        return ResponseEntity
                .ok()
                .headers(responseHeaders)
                .body(TokenResponse(refreshToken = tokenResult.refreshTokenValue))
    }

    @PostMapping("/me/logout")
    fun logout(@WithUserInfo userInfo: UserInfo) {
        authService.logout(userId = userInfo.userId)
    }

    private fun createAuthorizationHeader(tokenResult: TokenResult): HttpHeaders {
        val responseHeaders = HttpHeaders()
        responseHeaders.set(jwtConfig.header, "${jwtConfig.tokenPrefix}${tokenResult.accessTokenValue}")
        return responseHeaders
    }
}
