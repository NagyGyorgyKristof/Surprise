package hu.ngykristof.surprise.userapi

import hu.ngykristof.surprise.userapi.dto.NewUserRequest
import hu.ngykristof.surprise.userapi.dto.UpdateUserRequest
import hu.ngykristof.surprise.userapi.dto.UserDetailsResponse
import hu.ngykristof.surprise.userapi.dto.loginvalidation.ValidateUserLoginRequest
import hu.ngykristof.surprise.userapi.dto.loginvalidation.ValidateUserLoginResponse
import org.springframework.cloud.openfeign.FeignClient
import org.springframework.web.bind.annotation.*

@FeignClient("user-service")
interface UserFeignClient {

    @PostMapping("/users/register")
    fun registerNewUser(@RequestBody userRequest: NewUserRequest)

    @PostMapping("/users/validate-login")
    fun validateLogin(@RequestBody validateUserLoginRequest: ValidateUserLoginRequest): ValidateUserLoginResponse

    @GetMapping("/users/details/{userId}")
    fun getUserDetails(@PathVariable("userId") userId: String): UserDetailsResponse

    @PutMapping("/users/details/{userId}")
    fun updateUser(@RequestBody userRequest: UpdateUserRequest, @PathVariable("userId") userId: String)
}