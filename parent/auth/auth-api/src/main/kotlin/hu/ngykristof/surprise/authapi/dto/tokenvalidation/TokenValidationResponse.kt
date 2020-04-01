package hu.ngykristof.surprise.authapi.dto.tokenvalidation

class TokenValidationResponse(
        var isValid: Boolean = false,
        var errorMessage: String? = ""
)
