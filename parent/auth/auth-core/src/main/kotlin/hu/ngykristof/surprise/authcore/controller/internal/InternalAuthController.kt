package hu.ngykristof.surprise.authcore.controller.internal

import hu.ngykristof.surprise.authapi.dto.tokenvalidation.TokenValidationRequest
import hu.ngykristof.surprise.authapi.dto.tokenvalidation.TokenValidationResponse
import hu.ngykristof.surprise.authcore.service.AuthService
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
class InternalAuthController(
        private val authService: AuthService
) {

    @PostMapping("/validate-access-token")
    fun validateAccessToken(@RequestBody validateTokenRequest: TokenValidationRequest): TokenValidationResponse {
        return authService.validateAccessToken(validateTokenRequest)
    }

}
