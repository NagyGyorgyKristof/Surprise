package hu.ngykristof.surprise.authcore.service

import hu.ngykristof.surprise.authcore.domain.RefreshTokenEntity
import hu.ngykristof.surprise.authcore.error.TokenVerificationException
import hu.ngykristof.surprise.authcore.error.UserLoginFailedException
import hu.ngykristof.surprise.authcore.repository.TokenRepository
import hu.ngykristof.surprise.authcore.security.jwt.TokenProvider
import hu.ngykristof.surprise.authcore.test.*
import hu.ngykristof.surprise.userapi.UserFeignClient
import hu.ngykristof.surprise.userapi.dto.loginvalidation.ValidateUserLoginRequest
import io.mockk.*
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

internal class AuthServiceTest {

    private lateinit var userFeignClient: UserFeignClient
    private lateinit var tokenProvider: TokenProvider
    private lateinit var tokenRepository: TokenRepository
    private lateinit var authService: AuthService

    @BeforeEach
    internal fun setUp() {
        this.userFeignClient = mockk(relaxed = true)
        this.tokenProvider = mockk(relaxed = true)
        this.tokenRepository = mockk(relaxed = true)
        this.authService = AuthService(
                userFeignClient = userFeignClient,
                tokenProvider = tokenProvider,
                tokenRepository = tokenRepository
        )


    }

    @Test
    fun loginUser() {
        val mockUserId = "userId"
        val mockLoginRequest = createMockLoginRequest()
        val mockCoreUserInfo = createMockCoreUserInfoResponse(mockUserId)
        val mockRefreshTokenEntity = createMockRefreshTokenEntity(mockUserId)
        val mockAccessToken = "accessToken"
        val mockRefreshTokenResult = createMockRefreshTokenResult(mockUserId)
        val refreshTokenSlot = slot<RefreshTokenEntity>()

        every { userFeignClient.validateLogin(ofType(ValidateUserLoginRequest::class)) } returns mockCoreUserInfo
        every { tokenRepository.findOneByUserId(mockCoreUserInfo.userId) } returns mockRefreshTokenEntity
        every { tokenProvider.createAccessToken(mockCoreUserInfo) } returns mockAccessToken
        every { tokenProvider.createRefreshToken(mockCoreUserInfo) } returns mockRefreshTokenResult
        every { tokenRepository.save(ofType(RefreshTokenEntity::class)) } answers { firstArg() }

        val tokenResult = authService.loginUser(mockLoginRequest)

        verify { tokenRepository.delete(mockRefreshTokenEntity) }
        verify { tokenRepository.flush() }
        verify { tokenRepository.save(capture(refreshTokenSlot)) }

        assertEquals(mockUserId, refreshTokenSlot.captured.userId)
        assertEquals(mockRefreshTokenResult.value, refreshTokenSlot.captured.value)
        assertEquals(mockAccessToken, tokenResult.accessTokenValue)
        assertEquals(mockRefreshTokenResult.value, tokenResult.refreshTokenValue)
    }

    @Test
    fun loginUser_WithWrongCredentials() {
        val mockLoginRequest = createMockLoginRequest()

        every { userFeignClient.validateLogin(ofType(ValidateUserLoginRequest::class)) } throws Exception()

        assertThrows<UserLoginFailedException> { authService.loginUser(mockLoginRequest) }
    }

    @Test
    fun renewToken() {
        val mockUserId = "userId"
        val accessTokenRenewalMessage = createMockAccessTokenRenewalRequest()
        val mockRefreshTokenEntity = createMockRefreshTokenEntity(mockUserId)
        val mockCoreUserInfo = createMockCoreUserInfoResponse(mockUserId)
        val mockAccessToken = "accessToken"
        val mockRefreshTokenResult = createMockRefreshTokenResult(mockUserId)
        val refreshTokenSlot = slot<RefreshTokenEntity>()

        every { tokenRepository.findByValue(any()) } returns mockRefreshTokenEntity
        every { tokenProvider.verifyRefreshToken(any()) } just Runs
        every { userFeignClient.getCoreUserInfoForToken(mockUserId) } returns mockCoreUserInfo
        every { tokenProvider.createAccessToken(mockCoreUserInfo) } returns mockAccessToken
        every { tokenProvider.createRefreshToken(mockCoreUserInfo) } returns mockRefreshTokenResult
        every { tokenRepository.save(ofType(RefreshTokenEntity::class)) } answers { firstArg() }

        authService.renewToken(accessTokenRenewalMessage)

        verify { tokenRepository.delete(mockRefreshTokenEntity) }
        verify { tokenRepository.flush() }
        verify { tokenRepository.save(capture(refreshTokenSlot)) }

        assertEquals(mockUserId, refreshTokenSlot.captured.userId)
        assertEquals(mockRefreshTokenResult.value, refreshTokenSlot.captured.value)
    }

    @Test
    fun renewToken_WithWrongValue() {
        val accessTokenRenewalMessage = createMockAccessTokenRenewalRequest()

        every { tokenRepository.findByValue(any()) } returns null

        assertThrows<TokenVerificationException> { authService.renewToken(accessTokenRenewalMessage) }
    }

    @Test
    fun renewToken_WithExpiredToken() {
        val mockUserId = "userId"
        val accessTokenRenewalMessage = createMockAccessTokenRenewalRequest()
        val mockRefreshTokenEntity = createMockRefreshTokenEntity(mockUserId)

        every { tokenRepository.findByValue(any()) } returns mockRefreshTokenEntity
        every { tokenProvider.verifyRefreshToken(any()) } throws TokenVerificationException("")

        assertThrows<TokenVerificationException> { authService.renewToken(accessTokenRenewalMessage) }
    }

    @Test
    fun logout() {
        val mockUserId = "userId"
        val mockRefreshTokenEntity = createMockRefreshTokenEntity(mockUserId)

        every { tokenRepository.findOneByUserId(mockUserId) } returns mockRefreshTokenEntity

        authService.logout(mockUserId)

        verify { tokenRepository.delete(mockRefreshTokenEntity) }
        verify { tokenRepository.flush() }
    }

    @Test
    fun cleanExpiredTokens() {
        val mockUserId = "userId"
        val mockRefreshTokenEntity = createMockRefreshTokenEntity(mockUserId)
        val refreshTokens = listOf(mockRefreshTokenEntity)

        every { tokenRepository.findAllByExpirationDateBefore(any()) } returns refreshTokens

        authService.cleanExpiredTokens()

        verify { tokenRepository.deleteAll(refreshTokens) }
        verify { tokenRepository.flush() }
    }

    @Test
    fun validateAccessToken() {
        val accessToken = "accessToken"
        val validateTokenRequest = createMockTokenValidationRequest(accessToken)

        every { tokenProvider.verifyAccessToken(accessToken) } just Runs

        val validationResponse = authService.validateAccessToken(validateTokenRequest)

        assertTrue(validationResponse.isValid)
    }

    @Test
    fun validateAccessToken_WithExpiredToken() {
        val accessToken = "accessToken"
        val validateTokenRequest = createMockTokenValidationRequest(accessToken)

        every { tokenProvider.verifyAccessToken(accessToken) } throws Exception()

        val validationResponse = authService.validateAccessToken(validateTokenRequest)

        assertFalse(validationResponse.isValid)
    }

}