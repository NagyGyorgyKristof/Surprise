package hu.ngykristof.surprise.usercore.service

import hu.ngykristof.surprise.commonscore.extensions.orNull
import hu.ngykristof.surprise.userapi.dto.NewUserRequest
import hu.ngykristof.surprise.userapi.dto.UpdateUserRequest
import hu.ngykristof.surprise.userapi.dto.UserDetailsResponse
import hu.ngykristof.surprise.userapi.dto.loginvalidation.ValidateUserLoginRequest
import hu.ngykristof.surprise.userapi.dto.loginvalidation.ValidateUserLoginResponse
import hu.ngykristof.surprise.usercore.domain.UserEntity
import hu.ngykristof.surprise.usercore.error.*
import hu.ngykristof.surprise.usercore.mapper.toEntity
import hu.ngykristof.surprise.usercore.mapper.toUpdatedUserEntity
import hu.ngykristof.surprise.usercore.mapper.toUserDetailsResponse
import hu.ngykristof.surprise.usercore.mapper.toValidateUserLoginResponse
import hu.ngykristof.surprise.usercore.repository.UserRepository
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service


@Service
class UserService(
        private val userRepository: UserRepository,
        private val encoder: PasswordEncoder
) {

    fun registerNewUser(newUserRequest: NewUserRequest) {

        if (newUserRequest.username.isUsernameAlreadyUsed()) {
            throw UsernameAlreadyUsedException()
        }

        if (newUserRequest.email.isEmailAlreadyUsed()) {
            throw EmailAlreadyUsedException()
        }

        if (newUserRequest.email.isEmailInvalid()) {
            throw InvalidEmailException()
        }

        userRepository.save(newUserRequest.toEntity(encoder))
    }

    fun validateUserLogin(validateUserLoginRequest: ValidateUserLoginRequest): ValidateUserLoginResponse {
        val user = userRepository
                .findByUsername(validateUserLoginRequest.username)
                ?: throw WrongUsernameException()

        return user.checkPassword(validateUserLoginRequest.password).toValidateUserLoginResponse()
    }

    fun getUserDetails(userId: String): UserDetailsResponse {
        val user = userRepository.findById(userId).orNull() ?: throw UserNotFoundException()
        return user.toUserDetailsResponse()
    }

    fun updateUserDetails(updateUserRequest: UpdateUserRequest, userId: String) {
        val user = userRepository.findById(userId).orNull() ?: throw UserNotFoundException()

        if (updateUserRequest.username.isUsernameAlreadyUsed()) {
            throw UsernameAlreadyUsedException()
        }


        if (updateUserRequest.username.isUsernameAlreadyUsed()) {
            throw UsernameAlreadyUsedException()
        }

        if (updateUserRequest.email.isEmailAlreadyUsed()) {
            throw EmailAlreadyUsedException()
        }

        if (updateUserRequest.email.isEmailInvalid()) {
            throw InvalidEmailException()
        }

        userRepository.save(user.toUpdatedUserEntity(updateUserRequest))
    }

    //region support extensions
    private fun String.isEmailInvalid() = !this.contains("@")

    private fun String.isUsernameAlreadyUsed(): Boolean {
        val registeredUser = userRepository.findByUsername(this)
        return registeredUser != null
    }

    private fun String.isEmailAlreadyUsed(): Boolean {
        val registeredUser = userRepository.findByUsername(this)
        return registeredUser != null
    }

    private fun UserEntity.checkPassword(password: String): UserEntity {
        if (!encoder.matches(password, this.password)) {
            throw WrongPasswordException()
        }
        return this
    }
    //endregion
}
