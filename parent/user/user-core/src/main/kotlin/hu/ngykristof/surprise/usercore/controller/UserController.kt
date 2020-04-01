package hu.ngykristof.surprise.usercore.controller

import hu.ngykristof.surprise.commonscore.config.jwt.WithUserInfo
import hu.ngykristof.surprise.commonscore.dto.UserInfo
import hu.ngykristof.surprise.userapi.dto.NewUserRequest
import hu.ngykristof.surprise.userapi.dto.ResendActivationEmailRequest
import hu.ngykristof.surprise.userapi.dto.UpdateUserRequest
import hu.ngykristof.surprise.userapi.dto.UserDetailsResponse
import hu.ngykristof.surprise.userapi.dto.loginvalidation.CoreUserInfoResponse
import hu.ngykristof.surprise.userapi.dto.loginvalidation.ValidateUserLoginRequest
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

    @GetMapping("/core-info/{userId}")
    fun getCoreUserInfoForToken(@PathVariable("userId") userId: String): CoreUserInfoResponse {
        return userService.getCoreUserInfoForToken(userId)
    }

    @GetMapping("/activate")
    fun activateUserAccount(@RequestParam(value = "key") key: String) {
        userService.activateUserAccount(key)
    }

    @PostMapping("/validate-login")
    fun validateLogin(@RequestBody validateUserLoginRequest: ValidateUserLoginRequest): CoreUserInfoResponse {
        return userService.validateUserLogin(validateUserLoginRequest)
    }

    @GetMapping("/me")
    fun getCurrentUser(@WithUserInfo userInfo: UserInfo): UserDetailsResponse {
        return userService.getUserDetails(userId = userInfo.userId)
    }

    @PutMapping("/me")
    fun updateCurrentUser(@RequestBody userRequest: UpdateUserRequest, @WithUserInfo userInfo: UserInfo) {
        userService.updateUserDetails(
                updateUserRequest = userRequest,
                userId = userInfo.userId
        )
    }

    @DeleteMapping("/me")
    fun deleteCurrentUser(@WithUserInfo userInfo: UserInfo) {
        userService.deleteUser(userId = userInfo.userId)
    }
}
