package hu.ngykristof.surprise.authcore.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder

@Configuration
class PasswordEncoderConfiguration {


    @Bean
    fun passwordEncoder(): PasswordEncoder = BCryptPasswordEncoder()
}