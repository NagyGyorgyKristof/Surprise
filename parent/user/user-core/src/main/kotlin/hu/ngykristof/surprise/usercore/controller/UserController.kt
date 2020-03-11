package hu.ngykristof.surprise.usercore.controller

import hu.ngykristof.surprise.userapi.dto.NewUserRequest
import hu.ngykristof.surprise.userapi.dto.ResendActivationEmailRequest
import hu.ngykristof.surprise.userapi.dto.UpdateUserRequest
import hu.ngykristof.surprise.userapi.dto.UserDetailsResponse
import hu.ngykristof.surprise.userapi.dto.loginvalidation.ValidateUserLoginRequest
import hu.ngykristof.surprise.userapi.dto.loginvalidation.ValidateUserLoginResponse
import hu.ngykristof.surprise.usercore.service.UserService
import org.springframework.web.bind.annotation.*

@RestController
class UserController(
        private val userService: UserService
) {

    @PostMapping("/register")
    fun registerNewUser(@RequestBody userRequest: NewUserRequest) {
        userService.registerNewUser(userRequest)
    }

    @PostMapping("/resend-activation-email")
    fun registerNewUser(@RequestBody resendActivationEmailRequest: ResendActivationEmailRequest) {
        userService.resendActivationEmail(resendActivationEmailRequest)
    }

    @GetMapping("/activate")
    fun activateUserAccount(@RequestParam(value = "key") key: String) {
        userService.activateUserAccount(key)
    }

    @PostMapping("/validate-login")
    fun validateLogin(@RequestBody validateUserLoginRequest: ValidateUserLoginRequest): ValidateUserLoginResponse {
        return userService.validateUserLogin(validateUserLoginRequest)
    }

    @GetMapping("/details/{userId}")
    fun getUserDetails(@PathVariable("userId") userId: String): UserDetailsResponse {
        return userService.getUserDetails(userId)
    }

    @PutMapping("/details/{userId}")
    fun updateUserDetails(@RequestBody userRequest: UpdateUserRequest, @PathVariable("userId") userId: String) {
        userService.updateUserDetails(userRequest, userId)
    }
}