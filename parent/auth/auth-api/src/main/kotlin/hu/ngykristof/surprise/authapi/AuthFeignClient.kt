package hu.ngykristof.surprise.authapi

import hu.ngykristof.surprise.authapi.dto.login.LoginRequest
import hu.ngykristof.surprise.authapi.dto.login.LoginResponse
import hu.ngykristof.surprise.authapi.dto.tokenvalidation.TokenValidationRequest
import hu.ngykristof.surprise.authapi.dto.tokenvalidation.TokenValidationResponse
import org.springframework.cloud.openfeign.FeignClient
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody

@FeignClient("auth-service")
interface AuthFeignClient {

    @PostMapping("/auth/validate")
    fun validateToken(@RequestBody validateTokenRequest: TokenValidationRequest): TokenValidationResponse

    @PostMapping("/auth/login")
    fun login(@RequestBody loginRequest: LoginRequest): LoginResponse
}
