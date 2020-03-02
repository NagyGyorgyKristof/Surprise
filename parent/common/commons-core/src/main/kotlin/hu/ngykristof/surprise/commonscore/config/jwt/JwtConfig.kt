package hu.ngykristof.surprise.commonscore.config.jwt

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.stereotype.Component

@Component
@ConfigurationProperties("surprise.auth.jwt")
open class JwtConfig {
    lateinit var header: String
    lateinit var tokenPrefix: String
    lateinit var expiration: String
    lateinit var secret: String
    lateinit var rolePrefix: String
}