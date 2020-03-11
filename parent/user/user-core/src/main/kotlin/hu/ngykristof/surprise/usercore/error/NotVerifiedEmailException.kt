package hu.ngykristof.surprise.usercore.error

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus
import java.lang.RuntimeException


@ResponseStatus(code = HttpStatus.BAD_REQUEST, reason = "Login failed due to not verified email")
class NotVerifiedEmailException : RuntimeException() {
}