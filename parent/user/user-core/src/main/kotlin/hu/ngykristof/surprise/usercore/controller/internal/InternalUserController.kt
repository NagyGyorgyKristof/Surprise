package hu.ngykristof.surprise.usercore.controller.internal

import hu.ngykristof.surprise.userapi.dto.loginvalidation.CoreUserInfoResponse
import hu.ngykristof.surprise.userapi.dto.loginvalidation.ValidateUserLoginRequest
import hu.ngykristof.surprise.usercore.service.UserService
import org.springframework.web.bind.annotation.*

@RestController
class InternalUserController(
        private val userService: UserService
) {

    @GetMapping("/core-info/{userId}")
    fun getCoreUserInfoForToken(@PathVariable("userId") userId: String): CoreUserInfoResponse {
        return userService.getCoreUserInfoForToken(userId)
    }

    @PostMapping("/validate-login")
    fun validateLogin(@RequestBody validateUserLoginRequest: ValidateUserLoginRequest): CoreUserInfoResponse {
        return userService.validateUserLogin(validateUserLoginRequest)
    }


}
