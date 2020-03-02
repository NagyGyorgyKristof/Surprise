package hu.ngykristof.surprise.authapi.dto.tokenvalidation

data class ValidateTokenRequest(
        val accessToken: String = ""
)