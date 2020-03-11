package hu.ngykristof.surprise.usercore.error

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus

@ResponseStatus(code = HttpStatus.NOT_FOUND, reason = "No user was found for this activation key")
class ActivationKeyNotFoundException : RuntimeException()