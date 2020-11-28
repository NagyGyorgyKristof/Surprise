package hu.ngykristof.surprise.authcore.test

import hu.ngykristof.surprise.authapi.dto.login.LoginRequest
import hu.ngykristof.surprise.authapi.dto.renewtoken.AccessTokenRenewalRequest
import hu.ngykristof.surprise.authapi.dto.tokenvalidation.TokenValidationRequest
import hu.ngykristof.surprise.authcore.domain.RefreshTokenEntity
import hu.ngykristof.surprise.authcore.service.messages.RefreshTokenResult
import hu.ngykristof.surprise.userapi.dto.loginvalidation.CoreUserInfoResponse
import java.time.OffsetDateTime

fun createMockLoginRequest() = LoginRequest()

fun createMockCoreUserInfoResponse(userId: String) = CoreUserInfoResponse(userId = userId)

fun createMockRefreshTokenEntity(userId: String) = RefreshTokenEntity(userId = userId,
        expirationDate = OffsetDateTime.now().plusMonths(1),
        value = "")

fun createMockRefreshTokenResult(userId: String) = RefreshTokenResult(userId = userId,
        expirationDate = OffsetDateTime.now().plusMonths(1),
        value = "")

fun createMockAccessTokenRenewalRequest() = AccessTokenRenewalRequest()

fun createMockTokenValidationRequest(accessToken: String) = TokenValidationRequest(token = accessToken)


