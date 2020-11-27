package hu.ngykristof.surprise.usercore.service

import hu.ngykristof.surprise.commonscore.extensions.orNull
import hu.ngykristof.surprise.usercore.domain.UserEntity
import hu.ngykristof.surprise.usercore.error.*
import hu.ngykristof.surprise.usercore.repository.UserRepository
import hu.ngykristof.surprise.usercore.test.*
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.security.crypto.password.PasswordEncoder

internal class UserServiceTest {

    lateinit var passwordEncoder: PasswordEncoder
    lateinit var mailService: MailService
    lateinit var userService: UserService
    lateinit var userRepository: UserRepository

    @BeforeEach
    fun setUp() {
        passwordEncoder = mockk(relaxed = true)
        mailService = mockk(relaxed = true)
        userRepository = mockk(relaxed = true)
        userService = UserService(
                userRepository = userRepository,
                encoder = passwordEncoder,
                mailService = mailService
        )
    }

    @Test
    fun registerNewUser() {
        val mockNewUserRequest = createMockNewUserRequest()
        val userEntitySlot = slot<UserEntity>()

        every { userRepository.findOneByUsername(mockNewUserRequest.username) } returns null
        every { userRepository.findOneByEmailIgnoreCase(mockNewUserRequest.email) } returns null
        every { userRepository.save(ofType(UserEntity::class)) } answers { firstArg() }

        userService.registerNewUser(mockNewUserRequest)

        verify { userRepository.save(capture(userEntitySlot)) }
        verify { mailService.sendActivationEmail(ofType(UserEntity::class)) }

        assertEquals(mockNewUserRequest.email, userEntitySlot.captured.email)
        assertEquals(mockNewUserRequest.username, userEntitySlot.captured.username)
        assertEquals(mockNewUserRequest.firstName, userEntitySlot.captured.firstName)
        assertEquals(mockNewUserRequest.lastName, userEntitySlot.captured.lastName)
        assertFalse(userEntitySlot.captured.isActive)
    }

    @Test
    fun registerNewUser_WhenUsernameAlreadyUsedByAnInactiveUser() {
        val mockNewUserRequest = createMockNewUserRequest()
        val mockUserEntity = createMockUserEntity(isActive = false)
        val userEntitySlot = slot<UserEntity>()

        every { userRepository.findOneByUsername(mockNewUserRequest.username) } returns mockUserEntity
        every { userRepository.findOneByEmailIgnoreCase(mockNewUserRequest.email) } returns null
        every { userRepository.save(ofType(UserEntity::class)) } answers { firstArg() }

        userService.registerNewUser(mockNewUserRequest)

        verify { userRepository.save(capture(userEntitySlot)) }
        verify { mailService.sendActivationEmail(ofType(UserEntity::class)) }

        assertEquals(mockNewUserRequest.email, userEntitySlot.captured.email)
        assertEquals(mockNewUserRequest.username, userEntitySlot.captured.username)
        assertEquals(mockNewUserRequest.firstName, userEntitySlot.captured.firstName)
        assertEquals(mockNewUserRequest.lastName, userEntitySlot.captured.lastName)
        assertFalse(userEntitySlot.captured.isActive)
    }

    @Test
    fun registerNewUser_WhenUsernameAlreadyUsedByAnActiveUser() {
        val mockNewUserRequest = createMockNewUserRequest()
        val mockUserEntity = createMockUserEntity(isActive = true)

        every { userRepository.findOneByUsername(mockNewUserRequest.username) } returns mockUserEntity

        assertThrows<UsernameAlreadyUsedException> { userService.registerNewUser(mockNewUserRequest) }
    }

    @Test
    fun registerNewUser_WhenEmailAlreadyUsedByAnInactiveUser() {
        val mockNewUserRequest = createMockNewUserRequest()
        val mockUserEntity = createMockUserEntity(isActive = false)
        val userEntitySlot = slot<UserEntity>()

        every { userRepository.findOneByUsername(mockNewUserRequest.username) } returns null
        every { userRepository.findOneByEmailIgnoreCase(mockNewUserRequest.email) } returns mockUserEntity
        every { userRepository.save(ofType(UserEntity::class)) } answers { firstArg() }

        userService.registerNewUser(mockNewUserRequest)

        verify { userRepository.save(capture(userEntitySlot)) }
        verify { mailService.sendActivationEmail(ofType(UserEntity::class)) }

        assertEquals(mockNewUserRequest.email, userEntitySlot.captured.email)
        assertEquals(mockNewUserRequest.username, userEntitySlot.captured.username)
        assertEquals(mockNewUserRequest.firstName, userEntitySlot.captured.firstName)
        assertEquals(mockNewUserRequest.lastName, userEntitySlot.captured.lastName)
        assertFalse(userEntitySlot.captured.isActive)
    }

    @Test
    fun registerNewUser_WhenEmailAlreadyUsedByAnActiveUser() {
        val mockNewUserRequest = createMockNewUserRequest()
        val mockUserEntity = createMockUserEntity(isActive = true)

        every { userRepository.findOneByEmailIgnoreCase(mockNewUserRequest.email) } returns mockUserEntity

        assertThrows<EmailAlreadyUsedException> { userService.registerNewUser(mockNewUserRequest) }
    }


    @Test
    fun resendActivationEmail() {
        val mockResendActivationEmailRequest = createMockResendActivationEmailRequest()
        val mockUserEntity = createMockUserEntity(isActive = true)

        every { userRepository.findOneByEmailIgnoreCase(mockResendActivationEmailRequest.email) } returns mockUserEntity

        userService.resendActivationEmail(mockResendActivationEmailRequest)

        verify { mailService.sendActivationEmail(ofType(UserEntity::class)) }
    }

    @Test
    fun resendActivationEmail_WhenEmailDoesNotProper() {
        val mockResendActivationEmailRequest = createMockResendActivationEmailRequest()

        every { userRepository.findOneByEmailIgnoreCase(mockResendActivationEmailRequest.email) } returns null

        assertThrows<UserNotFoundException> { userService.resendActivationEmail(mockResendActivationEmailRequest) }

        verify(exactly = 0) { mailService.sendActivationEmail(ofType(UserEntity::class)) }
    }

    @Test
    fun activateUserAccount() {
        val mockActivationKey = "activationKey"
        val mockUserEntity = createMockUserEntity(isActive = true)
        val userEntitySlot = slot<UserEntity>()

        every { userRepository.findOneByActivationKey(mockActivationKey) } returns mockUserEntity
        every { userRepository.save(ofType(UserEntity::class)) } answers { firstArg() }

        userService.activateUserAccount(mockActivationKey)

        verify { userRepository.save(capture(userEntitySlot)) }

        assertTrue(userEntitySlot.captured.isActive)
    }

    @Test
    fun activateUserAccount_WhenActivationKeyNotProper() {
        val mockActivationKey = "activationKey"

        every { userRepository.findOneByActivationKey(mockActivationKey) } returns null

        assertThrows<ActivationKeyNotFoundException> { userService.activateUserAccount(mockActivationKey) }
    }

    @Test
    fun validateUserLogin() {
        val mockValidateUserLoginRequest = createMockValidationLoginRequest()
        val mockUserEntity = createMockUserEntity(isActive = true)

        every { userRepository.findOneByUsername(mockValidateUserLoginRequest.username) } returns mockUserEntity
        every { passwordEncoder.matches(any(), any()) } returns true

        val coreUserInfo = userService.validateUserLogin(mockValidateUserLoginRequest)

        assertEquals(mockUserEntity.username, coreUserInfo.username)
        assertEquals(mockUserEntity.roles, coreUserInfo.roles)
    }

    @Test
    fun validateUserLogin_WhenUsernameIsWrong() {
        val mockValidateUserLoginRequest = createMockValidationLoginRequest()

        every { userRepository.findOneByUsername(mockValidateUserLoginRequest.username) } returns null

        assertThrows<WrongUsernameException> { userService.validateUserLogin(mockValidateUserLoginRequest) }
    }

    @Test
    fun validateUserLogin_WhenPasswordIsWrong() {
        val mockValidateUserLoginRequest = createMockValidationLoginRequest()
        val mockUserEntity = createMockUserEntity(isActive = true)

        every { userRepository.findOneByUsername(mockValidateUserLoginRequest.username) } returns mockUserEntity
        every { passwordEncoder.matches(any(), any()) } returns false

        assertThrows<WrongPasswordException> { userService.validateUserLogin(mockValidateUserLoginRequest) }
    }

    @Test
    fun validateUserLogin_WhenUserIsNotActive() {
        val mockValidateUserLoginRequest = createMockValidationLoginRequest()
        val mockUserEntity = createMockUserEntity(isActive = false)

        every { userRepository.findOneByUsername(mockValidateUserLoginRequest.username) } returns mockUserEntity
        every { passwordEncoder.matches(any(), any()) } returns true

        assertThrows<NotVerifiedEmailException> { userService.validateUserLogin(mockValidateUserLoginRequest) }
    }

    @Test
    fun getCurrentUser() {
        val mockUserId = "userId"
        val mockUserEntity = createMockUserEntity(isActive = true)

        every { userRepository.findById(mockUserId).orNull() } returns mockUserEntity

        val currentUser = userService.getCurrentUser(mockUserId)

        assertEquals(currentUser.email, mockUserEntity.email)
        assertEquals(currentUser.username, mockUserEntity.username)
        assertEquals(currentUser.firstName, mockUserEntity.firstName)
        assertEquals(currentUser.lastName, mockUserEntity.lastName)
    }

    @Test
    fun getCurrentUser_WhenUserIsNotExist() {
        val mockUserId = "userId"

        every { userRepository.findById(mockUserId).orNull() } returns null

        assertThrows<UserNotFoundException> { userService.getCurrentUser(mockUserId) }
    }

    @Test
    fun updateCurrentUser() {
        val mockUpdateUserRequest = createMockUpdateUserRequest()
        val mockUserId = "userId"
        val mockUserEntity = createMockUserEntity(isActive = true)
        val userEntitySlot = slot<UserEntity>()

        every { userRepository.findById(mockUserId).orNull() } returns mockUserEntity
        every { userRepository.findOneByUsername(mockUpdateUserRequest.username) } returns null
        every { userRepository.findOneByEmailIgnoreCase(mockUpdateUserRequest.email) } returns null
        every { userRepository.save(ofType(UserEntity::class)) } answers { firstArg() }

        userService.updateCurrentUser(mockUpdateUserRequest, mockUserId)

        verify { userRepository.save(capture(userEntitySlot)) }

        assertEquals(mockUpdateUserRequest.email, userEntitySlot.captured.email)
        assertEquals(mockUpdateUserRequest.username, userEntitySlot.captured.username)
        assertEquals(mockUpdateUserRequest.firstName, userEntitySlot.captured.firstName)
        assertEquals(mockUpdateUserRequest.lastName, userEntitySlot.captured.lastName)
        assertTrue(userEntitySlot.captured.isActive)
    }

    @Test
    fun updateCurrentUser_WhenUserIdIsWrong() {
        val mockUpdateUserRequest = createMockUpdateUserRequest()
        val mockWrongUserId = "userId"

        every { userRepository.findById(mockWrongUserId).orNull() } returns null

        assertThrows<UserNotFoundException> { userService.updateCurrentUser(mockUpdateUserRequest, mockWrongUserId) }
    }

    @Test
    fun updateCurrentUser_WhenUsernameAlreadyUsedByAnActiveUser() {
        val mockUpdateUserRequest = createMockUpdateUserRequest()
        val mockUserId = "userId"
        val mockOtherUsername = "otherUsername"
        val mockUserEntity = createMockUserEntity(isActive = true)
        val mockOtherUserEntity = createMockUserEntity(isActive = true, username = mockOtherUsername)

        every { userRepository.findById(mockUserId).orNull() } returns mockUserEntity
        every { userRepository.findOneByUsername(mockUpdateUserRequest.username) } returns mockOtherUserEntity

        assertThrows<UsernameAlreadyUsedException> { userService.updateCurrentUser(mockUpdateUserRequest, mockUserId) }
    }

    @Test
    fun updateCurrentUser_WhenUsernameAlreadyUsedByAnInactiveUser() {
        val mockUpdateUserRequest = createMockUpdateUserRequest()
        val mockUserId = "userId"
        val mockUserEntity = createMockUserEntity(isActive = true)
        val mockOtherUserEntity = createMockUserEntity(isActive = false)
        val userEntitySlot = slot<UserEntity>()

        every { userRepository.findById(mockUserId).orNull() } returns mockUserEntity
        every { userRepository.findOneByUsername(mockUpdateUserRequest.username) } returns null
        every { userRepository.findOneByEmailIgnoreCase(mockUpdateUserRequest.email) } returns mockOtherUserEntity
        every { userRepository.save(ofType(UserEntity::class)) } answers { firstArg() }

        userService.updateCurrentUser(mockUpdateUserRequest, mockUserId)

        verify { userRepository.save(capture(userEntitySlot)) }

        assertEquals(mockUpdateUserRequest.email, userEntitySlot.captured.email)
        assertEquals(mockUpdateUserRequest.username, userEntitySlot.captured.username)
        assertEquals(mockUpdateUserRequest.firstName, userEntitySlot.captured.firstName)
        assertEquals(mockUpdateUserRequest.lastName, userEntitySlot.captured.lastName)
        assertTrue(userEntitySlot.captured.isActive)
    }

    @Test
    fun updateCurrentUser_WhenEmailAlreadyUsedByAnActiveUser() {
        val mockUpdateUserRequest = createMockUpdateUserRequest()
        val mockUserId = "userId"
        val mockOtherEmail = "otherEmail"
        val mockUserEntity = createMockUserEntity(isActive = true)
        val mockOtherUserEntity = createMockUserEntity(isActive = true, email = mockOtherEmail)

        every { userRepository.findById(mockUserId).orNull() } returns mockUserEntity
        every { userRepository.findOneByEmailIgnoreCase(mockUpdateUserRequest.username) } returns mockOtherUserEntity

        assertThrows<EmailAlreadyUsedException> { userService.updateCurrentUser(mockUpdateUserRequest, mockUserId) }
    }

    @Test
    fun deleteCurrentUser() {
        val mockUserId = "mockUserId"
        val mockUserEntity = createMockUserEntity()

        every { userRepository.findById(mockUserId).orNull() } returns mockUserEntity

        userService.deleteCurrentUser(mockUserId)

        verify { userRepository.delete(mockUserEntity) }
    }

    @Test
    fun deleteCurrentUser_WhenUserIdIsWrong() {
        val mockWrongUserId = "mockWrongUserId"

        every { userRepository.findById(mockWrongUserId).orNull() } returns null

        assertThrows<UserNotFoundException> { userService.deleteCurrentUser(mockWrongUserId) }
    }

    @Test
    fun removeNotActivatedUsers() {
        userService.removeNotActivatedUsers()

        verify { userRepository.findAllByIsActiveIsFalseAndActivationKeyIsNotNullAndCreationDateBefore(any()) }
    }

    @Test
    fun getCoreUserInfoForToken() {
        val mockUserId = "mockUserId"
        val mockUserEntity = createMockUserEntity()

        every { userRepository.findById(mockUserId).orNull() } returns mockUserEntity

        val coreUserInfoForToken = userService.getCoreUserInfoForToken(mockUserId)

        assertEquals(coreUserInfoForToken.username, mockUserEntity.username)
        assertEquals(coreUserInfoForToken.roles, mockUserEntity.roles)
    }

    @Test
    fun getCoreUserInfoForToken_WhenUserIdIsWrong() {
        val mockUserId = "mockUserId"

        every { userRepository.findById(mockUserId).orNull() } returns null

        assertThrows<UserNotFoundException> { userService.getCoreUserInfoForToken(mockUserId) }
    }
}
