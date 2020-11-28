package hu.ngykristof.surprise.authcore.service

import hu.ngykristof.surprise.authapi.dto.login.LoginRequest
import hu.ngykristof.surprise.authapi.dto.renewtoken.AccessTokenRenewalRequest
import hu.ngykristof.surprise.authapi.dto.tokenvalidation.TokenValidationRequest
import hu.ngykristof.surprise.authapi.dto.tokenvalidation.TokenValidationResponse
import hu.ngykristof.surprise.authcore.domain.RefreshTokenEntity
import hu.ngykristof.surprise.authcore.error.TokenVerificationException
import hu.ngykristof.surprise.authcore.error.UserLoginFailedException
import hu.ngykristof.surprise.authcore.mapper.toRefreshTokenEntity
import hu.ngykristof.surprise.authcore.repository.TokenRepository
import hu.ngykristof.surprise.authcore.security.jwt.TokenProvider
import hu.ngykristof.surprise.authcore.service.messages.TokenResult
import hu.ngykristof.surprise.commonscore.util.logger
import hu.ngykristof.surprise.userapi.UserFeignClient
import hu.ngykristof.surprise.userapi.dto.loginvalidation.CoreUserInfoResponse
import hu.ngykristof.surprise.userapi.dto.loginvalidation.ValidateUserLoginRequest
import org.springframework.stereotype.Service
import java.time.OffsetDateTime

@Service
class AuthService(
        private val userFeignClient: UserFeignClient,
        private val tokenProvider: TokenProvider,
        private val tokenRepository: TokenRepository
) {

    private val log = logger()

    fun loginUser(loginRequest: LoginRequest): TokenResult = try {

        val coreUserInfo = userFeignClient.validateLogin(ValidateUserLoginRequest(
                username = loginRequest.username,
                password = loginRequest.password)
        )
        log.info("Login was successful with username: ${coreUserInfo.username}")

        tokenRepository.findOneByUserId(coreUserInfo.userId)?.invalidate()
        log.info("The dangling refresh token was in invalidated successfully with userId : ${coreUserInfo.userId}")

        val tokenResult = createTokensForUser(coreUserInfo)
        log.info("Tokens were created successfully with useId: ${coreUserInfo.userId}")

        tokenResult
    } catch (e: Exception) {
        throw UserLoginFailedException()
    }

    fun renewToken(accessTokenRenewalMessage: AccessTokenRenewalRequest): TokenResult {
        val refreshTokenEntity = tokenRepository
                .findByValue(accessTokenRenewalMessage.refreshToken)
                .verifyExistence()
                .verifyExpirationDate()
                .invalidate()

        val coreUserInfo = userFeignClient.getCoreUserInfoForToken(refreshTokenEntity.userId)

        log.info("The expired refresh token was in invalidated successfully with userId: ${coreUserInfo.userId}")

        val tokenResult = createTokensForUser(coreUserInfo)
        log.info("Tokens were created successfully with useId: ${coreUserInfo.userId}")

        return tokenResult
    }


    fun logout(userId: String) {
        tokenRepository.findOneByUserId(userId)?.invalidate()
        log.info("The refresh token was in invalidated successfully with useId: $userId")
        log.info("The logout was successful with userId: $userId ")

    }

    fun cleanExpiredTokens() {
        val expiredRefreshTokens = tokenRepository.findAllByExpirationDateBefore(OffsetDateTime.now())
        expiredRefreshTokens.invalidateAll()
    }

    fun validateAccessToken(validateTokenRequest: TokenValidationRequest): TokenValidationResponse {
        return try {
            validateTokenRequest.token.verifyToken()

            TokenValidationResponse(isValid = true)
        } catch (e: Exception) {
            TokenValidationResponse(
                    isValid = false,
                    errorMessage = e.message
            )
        }
    }

    //region support extensions
    private fun RefreshTokenEntity.verifyExpirationDate(): RefreshTokenEntity {
        tokenProvider.verifyRefreshToken(this.expirationDate)
        return this
    }

    private fun String.verifyToken() {
        tokenProvider.verifyAccessToken(this)
    }

    private fun RefreshTokenEntity?.verifyExistence(): RefreshTokenEntity {
        return this ?: throw TokenVerificationException("Invalid refreshToken")
    }

    private fun RefreshTokenEntity.invalidate(): RefreshTokenEntity {
        tokenRepository.delete(this)
        tokenRepository.flush()
        return this
    }

    private fun List<RefreshTokenEntity>.invalidateAll() {
        tokenRepository.deleteAll(this)
        tokenRepository.flush()
    }
    //endregion

    private fun createTokensForUser(coreUserInfo: CoreUserInfoResponse): TokenResult {
        val accessToken = tokenProvider.createAccessToken(coreUserInfo)
        val refreshTokenResult = tokenProvider.createRefreshToken(coreUserInfo)

        tokenRepository.save(refreshTokenResult.toRefreshTokenEntity())

        return TokenResult(
                accessTokenValue = accessToken,
                refreshTokenValue = refreshTokenResult.value
        )
    }
}
