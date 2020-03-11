package hu.ngykristof.surprise.commonscore.interceptor

import hu.ngykristof.surprise.commomconfig.config.jwt.JwtConfig
import org.springframework.context.annotation.Configuration
import org.springframework.web.method.support.HandlerMethodArgumentResolver
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer

@Configuration
class UserInfoWebConfig(
        private val jwtConfig: JwtConfig
) : WebMvcConfigurer {


    override fun addArgumentResolvers(resolvers: MutableList<HandlerMethodArgumentResolver>) {
        resolvers.add(UserInfoHandlerMethodArgumentResolver(jwtConfig))
    }
}