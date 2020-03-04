package hu.ngykristof.surprise.usercore.mapper

import hu.ngykristof.surprise.userapi.dto.NewUserRequest
import hu.ngykristof.surprise.userapi.dto.UpdateUserRequest
import hu.ngykristof.surprise.userapi.dto.UserDetailsResponse
import hu.ngykristof.surprise.userapi.dto.loginvalidation.Role
import hu.ngykristof.surprise.userapi.dto.loginvalidation.ValidateUserLoginResponse
import hu.ngykristof.surprise.usercore.domain.RoleEntity
import hu.ngykristof.surprise.usercore.domain.UserEntity
import org.springframework.security.crypto.password.PasswordEncoder


fun NewUserRequest.toEntity(passwordEncoder: PasswordEncoder): UserEntity {
    return UserEntity(
            firstName = this.firstName,
            lastName = this.lastName,
            username = this.username,
            password = passwordEncoder.encode(this.password),
            email = this.email,
            roles = getRoles()
    )
}


fun UserEntity.toValidateUserLoginResponse(): ValidateUserLoginResponse {
    return ValidateUserLoginResponse(
            username = this.username,
            userId = this.id ?: "",
            roles = this.roles.map { it.toDTO() }
    )
}

fun UserEntity.toUserDetailsResponse(): UserDetailsResponse {
    return UserDetailsResponse(
            firstName = this.firstName,
            lastName = this.lastName,
            email = this.email,
            username = this.username
    )
}

fun UserEntity.toUpdatedUserEntity(updateUserRequest: UpdateUserRequest): UserEntity {
    return this.also {
        firstName = updateUserRequest.firstName
        lastName = updateUserRequest.lastName
        email = updateUserRequest.email
        username = updateUserRequest.username
    }


}


private fun NewUserRequest.getRoles(): List<RoleEntity> {
    return if (this.isAdmin) {
        listOf(RoleEntity.USER, RoleEntity.ADMIN)
    } else {
        listOf(RoleEntity.USER)
    }
}

private fun RoleEntity.toDTO(): Role {
    return when (this.ordinal) {
        1 -> Role.USER
        2 -> Role.ADMIN
        else -> Role.USER
    }
}
