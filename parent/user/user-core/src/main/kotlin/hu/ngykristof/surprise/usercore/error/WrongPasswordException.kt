package hu.ngykristof.surprise.usercore.error

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus


@ResponseStatus(code = HttpStatus.UNAUTHORIZED, reason = " Login failed due to wrong password")
class WrongPasswordException : RuntimeException() {
}