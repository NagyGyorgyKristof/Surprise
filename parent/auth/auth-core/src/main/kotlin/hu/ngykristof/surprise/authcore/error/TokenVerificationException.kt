package hu.ngykristof.surprise.authcore.error

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus


@ResponseStatus(code = HttpStatus.UNAUTHORIZED)
class TokenVerificationException(msg: String) : RuntimeException(msg, null, false, false)
