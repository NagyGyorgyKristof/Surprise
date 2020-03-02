package hu.ngykristof.surprise.authapi

import hu.ngykristof.surprise.authapi.dto.tokenvalidation.ValidateTokenRequest
import hu.ngykristof.surprise.authapi.dto.tokenvalidation.ValidateTokenResponse
import org.springframework.cloud.openfeign.FeignClient
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody

@FeignClient("auth-service")
interface AuthFeignClient {

    @PostMapping("/auth/validate")
    fun validateToken(@RequestBody validateTokenRequest: ValidateTokenRequest): ValidateTokenResponse
}
