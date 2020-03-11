package hu.ngykristof.surprise.usercore.error

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus

@ResponseStatus(code = HttpStatus.BAD_REQUEST, reason = "The email is already used")
class EmailAlreadyUsedException : RuntimeException() {
}