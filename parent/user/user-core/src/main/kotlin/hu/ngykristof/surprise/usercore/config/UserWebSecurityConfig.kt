package hu.ngykristof.surprise.usercore.config

import hu.ngykristof.surprise.commonscore.config.jwt.JwtConfig
import hu.ngykristof.surprise.commonscore.security.AuthorizationFilter
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter

@EnableWebSecurity
class UserWebSecurityConfig(
        private val jwtConfig: JwtConfig
) : WebSecurityConfigurerAdapter() {

    override fun configure(http: HttpSecurity) {
        http.authorizeRequests()
                .antMatchers("/register").permitAll()
                .antMatchers("/validate-login").permitAll()
                .antMatchers("/activate").permitAll()
                .antMatchers("/resend-activation-email").permitAll()
                .anyRequest().authenticated()
                .and()
                .addFilterBefore(AuthorizationFilter(jwtConfig),
                        BasicAuthenticationFilter::class.java)
                .csrf().disable()
    }
}