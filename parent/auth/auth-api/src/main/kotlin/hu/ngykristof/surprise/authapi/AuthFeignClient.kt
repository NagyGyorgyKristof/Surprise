package hu.ngykristof.surprise.authapi

import hu.ngykristof.surprise.authapi.dto.login.LoginRequest
import hu.ngykristof.surprise.authapi.dto.login.TokenResponse
import hu.ngykristof.surprise.authapi.dto.renewtoken.AccessTokenRenewalRequest
import hu.ngykristof.surprise.authapi.dto.tokenvalidation.TokenValidationRequest
import hu.ngykristof.surprise.authapi.dto.tokenvalidation.TokenValidationResponse
import org.springframework.cloud.openfeign.FeignClient
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody

@FeignClient("auth-service", decode404 = true)
interface AuthFeignClient {

    @PostMapping("/auth/validate")
    fun validateToken(@RequestBody validateTokenRequest: TokenValidationRequest): TokenValidationResponse

    @PostMapping("/auth/authenticate")
    fun login(@RequestBody loginRequest: LoginRequest): ResponseEntity<TokenResponse>

    @PostMapping("/auth/token")
    fun renewAccessToken(@RequestBody renewalRequest: AccessTokenRenewalRequest): ResponseEntity<TokenResponse>

    @PostMapping("/auth/me/logout")
    fun logout()
}


