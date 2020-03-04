package hu.ngykristof.surprise.authcore.error

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus

@ResponseStatus(code = HttpStatus.UNAUTHORIZED, reason = " Login failed")
class UserLoginFailedException : RuntimeException()