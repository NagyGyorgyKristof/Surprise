package hu.ngykristof.surprise.authcore.security

import hu.ngykristof.surprise.commonscore.config.jwt.JwtConfig
import hu.ngykristof.surprise.commonscore.security.AuthorizationFilter
import org.springframework.core.annotation.Order
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter
import org.springframework.stereotype.Component

@Component
@EnableWebSecurity
@Order(1)
class AuthWebSecurityConfig(
        private val jwtConfig: JwtConfig
) : WebSecurityConfigurerAdapter() {

    override fun configure(http: HttpSecurity) {
        http.authorizeRequests()
                .antMatchers("/me/logout").authenticated()
                .anyRequest().permitAll()
                .and()
                .addFilterBefore(AuthorizationFilter(jwtConfig),
                        BasicAuthenticationFilter::class.java)
                .csrf().disable()
    }
}
