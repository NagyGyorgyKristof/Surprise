package hu.ngykristof.surprise.usercore.service

import hu.ngykristof.surprise.commonscore.extensions.orNull
import hu.ngykristof.surprise.userapi.dto.NewUserRequest
import hu.ngykristof.surprise.userapi.dto.ResendActivationEmailRequest
import hu.ngykristof.surprise.userapi.dto.UpdateUserRequest
import hu.ngykristof.surprise.userapi.dto.UserDetailsResponse
import hu.ngykristof.surprise.userapi.dto.loginvalidation.CoreUserInfoResponse
import hu.ngykristof.surprise.userapi.dto.loginvalidation.ValidateUserLoginRequest
import hu.ngykristof.surprise.usercore.domain.UserEntity
import hu.ngykristof.surprise.usercore.error.*
import hu.ngykristof.surprise.usercore.mapper.*
import hu.ngykristof.surprise.usercore.repository.UserRepository
import org.slf4j.LoggerFactory
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import java.time.OffsetDateTime


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
        val user = userRepository.findOneByEmailIgnoreCase(request.email)
                .checkExistenceByEmail(request.email)
                .checkEmailVerification()

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

    fun validateUserLogin(validateUserLoginRequest: ValidateUserLoginRequest): CoreUserInfoResponse {
        val user = userRepository
                .findOneByUsername(validateUserLoginRequest.username)

        return user.checkUsername()
                .checkPasswordCorrectness(validateUserLoginRequest.password)
                .checkEmailVerification()
                .toCoreUserInfoResponse()
    }

    fun getCurrentUser(userId: String): UserDetailsResponse {
        val user = userRepository.findById(userId).orNull().checkExistenceById(userId)
        return user.toUserDetailsResponse()
    }

    fun updateCurrentUser(updateUserRequest: UpdateUserRequest, userId: String) {
        val user = userRepository.findById(userId).orNull().checkExistenceById(userId)

        if (updateUserRequest.username.isUsernameAlreadyUsed()
                        .and(updateUserRequest.username != user.username)) {
            throw UsernameAlreadyUsedException()
        }

        if (updateUserRequest.email.isEmailAlreadyUsed()
                        .and(updateUserRequest.email != user.email)) {
            throw EmailAlreadyUsedException()
        }

        val updatedUser = userRepository.save(user.toUpdatedUserEntity(updateUserRequest))
        log.debug("Changed Information for User: {}", updatedUser)
    }

    fun deleteCurrentUser(userId: String) {
        val user = userRepository.findById(userId).orNull().checkExistenceById(userId)
        userRepository.delete(user)
        log.debug("User has been deleted with userId: $userId")
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

    private fun UserEntity.checkPasswordCorrectness(password: String): UserEntity {
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

    private fun UserEntity?.checkUsername(): UserEntity {
        return this ?: throw WrongUsernameException()
    }

    private fun UserEntity?.checkExistenceById(userId: String): UserEntity {
        return this ?: throw UserNotFoundException("No user was found for this userId :$userId")
    }

    private fun UserEntity?.checkExistenceByResetKey(resetKey: String): UserEntity {
        return this ?: throw UserNotFoundException("No user was found for this reset key: $resetKey")
    }

    private fun UserEntity?.checkExistenceByActivationKey(activationKey: String): UserEntity {
        return this ?: throw UserNotFoundException("No user was found for this activation key: $activationKey")
    }

    private fun UserEntity?.checkExistenceByEmail(email: String): UserEntity {
        return this ?: throw UserNotFoundException("No user was found for this email: $email")
    }
    //endregion

    fun removeNotActivatedUsers() {
        userRepository
                .findAllByIsActiveIsFalseAndActivationKeyIsNotNullAndCreationDateBefore(OffsetDateTime.now().minusDays(3))
                .forEach { userRepository.delete(it) }
    }

    fun getCoreUserInfoForToken(userId: String) = userRepository.findById(userId).orNull()?.toCoreUserInfoResponse()
            ?: throw UserNotFoundException("No user was found for this userId: $userId")
}
