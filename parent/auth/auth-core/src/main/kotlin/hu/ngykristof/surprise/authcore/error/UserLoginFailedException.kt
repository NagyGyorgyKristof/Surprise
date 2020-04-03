package hu.ngykristof.surprise.authcore.error

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus

@ResponseStatus(code = HttpStatus.UNAUTHORIZED, reason = " Login failed due to wrong username or password or the use is not activated")
class UserLoginFailedException() : RuntimeException()
