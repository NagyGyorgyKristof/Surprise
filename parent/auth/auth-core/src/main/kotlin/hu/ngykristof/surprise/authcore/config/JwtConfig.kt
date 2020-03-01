package hu.ngykristof.surprise.authcore.config

import org.springframework.boot.context.properties.ConfigurationProperties


@ConfigurationProperties("surprise.auth.jwt")
open class JwtConfig {
    lateinit var uri: String
    lateinit var header: String
    lateinit var prefix: String
    lateinit var expiration: String
    lateinit var secret: String
    lateinit var rolePrefix: String
}