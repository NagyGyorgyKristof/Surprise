package hu.ngykristof.surprise.authcore

import hu.ngykristof.surprise.authcore.config.JwtConfig
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.runApplication
import org.springframework.cloud.client.discovery.EnableDiscoveryClient
import org.springframework.cloud.openfeign.EnableFeignClients
import org.springframework.context.annotation.ComponentScan

@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients(basePackages = ["hu.ngykristof.surprise"])
@ComponentScan("hu.ngykristof.surprise")
@EnableConfigurationProperties(JwtConfig::class)
class AuthApplication

fun main(args: Array<String>) {
    runApplication<AuthApplication>(*args)
}


