package hu.ngykristof.surprise.authcore.controller

import hu.ngykristof.surprise.authapi.dto.tokenvalidation.ValidateTokenRequest
import hu.ngykristof.surprise.authapi.dto.tokenvalidation.ValidateTokenResponse
import hu.ngykristof.surprise.authcore.service.TokenService
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
class AuthController(
        private val tokenService: TokenService
) {


    @PostMapping("/validate")
    fun validateToken(@RequestBody validateTokenRequest: ValidateTokenRequest): ValidateTokenResponse {
        return tokenService.validateToken(validateTokenRequest)
    }
}