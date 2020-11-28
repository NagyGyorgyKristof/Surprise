package hu.ngykristof.surprise.usercore.test

import hu.ngykristof.surprise.userapi.dto.NewUserRequest
import hu.ngykristof.surprise.userapi.dto.ResendActivationEmailRequest
import hu.ngykristof.surprise.userapi.dto.UpdateUserRequest
import hu.ngykristof.surprise.userapi.dto.loginvalidation.Role
import hu.ngykristof.surprise.userapi.dto.loginvalidation.ValidateUserLoginRequest
import hu.ngykristof.surprise.usercore.domain.RoleEntity
import hu.ngykristof.surprise.usercore.domain.UserEntity

fun createMockNewUserRequest() = NewUserRequest(
        firstName = "firstName",
        lastName = "lastName",
        username = "username",
        password = "password",
        email = "test@email.com",
        roles = listOf(Role.USER)
)

fun createMockUserEntity(
        isActive: Boolean = true,
        username: String = "username",
        email: String = "test@email.com"
) = UserEntity(isActive = isActive, username = username, email = email, roles = listOf(RoleEntity.USER))

fun createMockResendActivationEmailRequest() = ResendActivationEmailRequest(
        email = "test@email.com"
)

fun createMockValidationLoginRequest() = ValidateUserLoginRequest()
fun createMockUpdateUserRequest() = UpdateUserRequest()