package hu.ngykristof.surprise.commonscore.security

import hu.ngykristof.surprise.commonscore.config.jwt.JwtConfig
import org.springframework.context.annotation.Configuration
import org.springframework.core.annotation.Order
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter

@Configuration
@EnableWebSecurity
@Order(1000)
//TODO refactor this code with kotlin DSL at 2.3.0 main version
class CommonWebSecurity(
        private val jwtConfig: JwtConfig
) : WebSecurityConfigurerAdapter() {

    override fun configure(http: HttpSecurity) {
        http.addFilterBefore(AuthorizationFilter(jwtConfig),
                BasicAuthenticationFilter::class.java)
        http.authorizeRequests()
                .antMatchers("/admin/**").hasRole("ADMIN")
                .anyRequest().authenticated()
                .and()
                .csrf().disable()
    }
}