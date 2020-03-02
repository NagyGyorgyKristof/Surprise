package hu.ngykristof.surprise.authcore.security

import hu.ngykristof.surprise.authcore.service.UserDetailsServiceImpl
import hu.ngykristof.surprise.commonscore.config.jwt.JwtConfig
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.crypto.password.PasswordEncoder


@EnableWebSecurity
class AuthWebSecurity(
        private val userDetailsServiceImpl: UserDetailsServiceImpl,
        private val jwtConfig: JwtConfig,
        private val passwordEncoder: PasswordEncoder
) : WebSecurityConfigurerAdapter() {

    override fun configure(http: HttpSecurity) {
        http.csrf().disable()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .addFilter(JwtAuthenticationFilter(authenticationManager(), jwtConfig))
                .authorizeRequests()
                .anyRequest().permitAll()
    }

    override fun configure(auth: AuthenticationManagerBuilder) {
        auth.userDetailsService(userDetailsServiceImpl).passwordEncoder(passwordEncoder)
    }
}