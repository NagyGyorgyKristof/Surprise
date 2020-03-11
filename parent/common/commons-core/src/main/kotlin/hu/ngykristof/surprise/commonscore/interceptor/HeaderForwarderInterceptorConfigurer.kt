package hu.ngykristof.surprise.commonscore.interceptor

import hu.ngykristof.surprise.commonscore.config.jwt.JwtConfig
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration


@Configuration
class HeaderForwarderInterceptorConfigurer(
        private val jwtConfig: JwtConfig
) {

    @Bean(name = ["authInfoForwarderInterceptor"])
    fun authInfoForwarderInterceptor(): HeaderForwarderInterceptor {
        return HeaderForwarderInterceptor(jwtConfig.header)
    }

    @Bean(name = ["acceptLanguageHeaderForwarderInterceptor"])
    fun acceptLanguageHeaderForwarderInterceptor(): HeaderForwarderInterceptor? {
        return HeaderForwarderInterceptor("Accept-Language")
    }

}