package hu.ngykristof.surprise.userapi

import hu.ngykristof.surprise.userapi.dto.NewUserRequest
import hu.ngykristof.surprise.userapi.dto.ResendActivationEmailRequest
import hu.ngykristof.surprise.userapi.dto.UpdateUserRequest
import hu.ngykristof.surprise.userapi.dto.UserDetailsResponse
import hu.ngykristof.surprise.userapi.dto.loginvalidation.CoreUserInfoResponse
import hu.ngykristof.surprise.userapi.dto.loginvalidation.ValidateUserLoginRequest
import org.springframework.cloud.openfeign.FeignClient
import org.springframework.web.bind.annotation.*

@FeignClient("user-service", decode404 = true)
interface UserFeignClient {

    @PostMapping("/users/register")
    fun registerNewUser(@RequestBody userRequest: NewUserRequest)

    @PostMapping("/users/validate-login")
    fun validateLogin(@RequestBody validateUserLoginRequest: ValidateUserLoginRequest): CoreUserInfoResponse

    @GetMapping("/users/me")
    fun getUserDetails(): UserDetailsResponse

    @PutMapping("/users/me")
    fun updateUser(@RequestBody userRequest: UpdateUserRequest)

    @DeleteMapping("/users/me")
    fun deleteUser()

    @GetMapping("/users/activate")
    fun activateUserAccount(@RequestParam(value = "key") key: String)

    @PostMapping("/users/resend-activation-email")
    fun registerNewUser(@RequestBody resendActivationEmailRequest: ResendActivationEmailRequest)

    @GetMapping("/users/core-info/{userId}")
    fun getCoreUserInfoForToken(@PathVariable("userId") userId: String): CoreUserInfoResponse


}
