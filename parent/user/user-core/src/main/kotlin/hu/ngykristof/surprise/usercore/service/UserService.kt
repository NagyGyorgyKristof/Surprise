package hu.ngykristof.surprise.usercore.service

import hu.ngykristof.surprise.commonscore.extensions.orNull
import hu.ngykristof.surprise.userapi.dto.NewUserRequest
import hu.ngykristof.surprise.userapi.dto.ResendActivationEmailRequest
import hu.ngykristof.surprise.userapi.dto.UpdateUserRequest
import hu.ngykristof.surprise.userapi.dto.UserDetailsResponse
import hu.ngykristof.surprise.userapi.dto.loginvalidation.ValidateUserLoginRequest
import hu.ngykristof.surprise.userapi.dto.loginvalidation.ValidateUserLoginResponse
import hu.ngykristof.surprise.usercore.domain.UserEntity
import hu.ngykristof.surprise.usercore.error.*
import hu.ngykristof.surprise.usercore.mapper.*
import hu.ngykristof.surprise.usercore.repository.UserRepository
import org.slf4j.LoggerFactory
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service


@Service
class UserService(
        private val userRepository: UserRepository,
        private val encoder: PasswordEncoder,
        private val mailService: MailService
) {

    private val log = LoggerFactory.getLogger(UserService::class.java)


    fun registerNewUser(newUserRequest: NewUserRequest) {

        if (newUserRequest.username.isUsernameAlreadyUsed()) {
            throw UsernameAlreadyUsedException()
        }

        if (newUserRequest.email.isEmailAlreadyUsed()) {
            throw EmailAlreadyUsedException()
        }

        val newUser = userRepository.save(newUserRequest.toEntity(encoder))
        mailService.sendActivationEmail(newUser)

        log.debug("Created Information for User: {}", newUser)
    }

    fun resendActivationEmail(request: ResendActivationEmailRequest) {
        val user = userRepository.findOneByUsername(request.username) ?: throw UserNotFoundException()
        mailService.sendActivationEmail(user)
        log.debug("Resend activation email for User: {}", user)
    }


    fun activateUserAccount(activationKey: String) {
        log.debug("Activating user for activation key {}", activationKey)

        val user = userRepository.findOneByActivationKey(activationKey)
                ?: throw ActivationKeyNotFoundException()
        userRepository.save(user.toActivatedUserEntity())

        log.debug("Activated user: {}", user)
    }

    fun validateUserLogin(validateUserLoginRequest: ValidateUserLoginRequest): ValidateUserLoginResponse {
        val user = userRepository
                .findOneByUsername(validateUserLoginRequest.username)
                ?: throw WrongUsernameException()

        return user
                .checkPassword(validateUserLoginRequest.password)
                .checkEmailVerification()
                .toValidateUserLoginResponse()
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

        if (updateUserRequest.email.isEmailAlreadyUsed()) {
            throw EmailAlreadyUsedException()
        }

        val updatedUser = userRepository.save(user.toUpdatedUserEntity(updateUserRequest))
        log.debug("Changed Information for User: {}", updatedUser)
    }

    //region support extensions

    private fun String.isUsernameAlreadyUsed(): Boolean {
        val existingUser = userRepository.findOneByUsername(this) ?: return false
        return removeNonActivatedUser(existingUser)
    }

    private fun String.isEmailAlreadyUsed(): Boolean {
        val existingUser = userRepository.findOneByEmailIgnoreCase(this) ?: return false
        return removeNonActivatedUser(existingUser)
    }

    private fun UserEntity.checkPassword(password: String): UserEntity {
        if (!encoder.matches(password, this.password)) {
            throw WrongPasswordException()
        }
        return this
    }

    private fun UserEntity.checkEmailVerification(): UserEntity {
        if (!this.isActive) {
            throw NotVerifiedEmailException()
        }
        return this
    }


    private fun removeNonActivatedUser(existingUser: UserEntity): Boolean {
        if (existingUser.isActive) {
            return true
        }

        userRepository.delete(existingUser)
        userRepository.flush()
        return false
    }
    //endregion
}
