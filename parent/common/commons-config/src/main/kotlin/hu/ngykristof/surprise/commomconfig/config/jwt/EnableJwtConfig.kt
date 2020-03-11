package hu.ngykristof.surprise.commomconfig.config.jwt

import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Import


@kotlin.annotation.Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.ANNOTATION_CLASS, AnnotationTarget.CLASS)
@Import(JwtConfig::class)
@EnableConfigurationProperties(JwtConfig::class)
annotation class EnableJwtConfig