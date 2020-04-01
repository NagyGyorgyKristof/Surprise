package hu.ngykristof.surprise.commonscore.config.jwt

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.stereotype.Component

@Component
@ConfigurationProperties("surprise.auth.jwt")
class JwtConfig {
    lateinit var header: String
    lateinit var tokenPrefix: String
    lateinit var tokenPrefixSize: String
    lateinit var accessTokenValidityInMins: String
    lateinit var refreshTokenValidityInDays: String
    lateinit var secret: String
    lateinit var rolePrefix: String
}
