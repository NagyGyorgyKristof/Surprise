package hu.ngykristof.surprise.commonscore.interceptor

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import hu.ngykristof.surprise.commomconfig.config.jwt.JwtConfig
import hu.ngykristof.surprise.commonscore.config.WithUserInfo
import hu.ngykristof.surprise.commonscore.dto.UserInfo
import org.springframework.core.MethodParameter
import org.springframework.web.bind.support.WebDataBinderFactory
import org.springframework.web.context.request.NativeWebRequest
import org.springframework.web.method.support.HandlerMethodArgumentResolver
import org.springframework.web.method.support.ModelAndViewContainer
import javax.servlet.http.HttpServletRequest


class UserInfoHandlerMethodArgumentResolver(
        private val jwtConfig: JwtConfig
) : HandlerMethodArgumentResolver {
    override fun supportsParameter(parameter: MethodParameter): Boolean {
        // We check if our parameter is exactly what we need:
        return parameter.hasParameterAnnotation(WithUserInfo::class.java) &&
                parameter.parameterType == UserInfo::class.java
    }

    override fun resolveArgument(parameter: MethodParameter, mavContainer: ModelAndViewContainer?, webRequest: NativeWebRequest, binderFactory: WebDataBinderFactory?): Any? {
        val httpServletRequest = webRequest.nativeRequest as HttpServletRequest
        val authorizationHeader = httpServletRequest.getHeader(jwtConfig.header)
        val token = authorizationHeader.replace(jwtConfig.tokenPrefix, "")

        val jwt = JWT
                .require(Algorithm.HMAC512(jwtConfig.secret.toByteArray()))
                .build()
                .verify(token.replace(jwtConfig.tokenPrefix, ""))

        val userId = jwt.subject
        val username = jwt.getClaim("username").asString()

        return UserInfo(userId, username)
    }
}