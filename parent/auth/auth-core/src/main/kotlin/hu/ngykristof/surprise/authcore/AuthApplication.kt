package hu.ngykristof.surprise.authcore

import hu.ngykristof.surprise.commomconfig.config.jwt.EnableJwtConfig
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.cloud.client.discovery.EnableDiscoveryClient
import org.springframework.cloud.openfeign.EnableFeignClients
import org.springframework.context.annotation.ComponentScan

@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients(basePackages = ["hu.ngykristof.surprise"])
@ComponentScan("hu.ngykristof.surprise")
@EnableJwtConfig
class AuthApplication

fun main(args: Array<String>) {
    runApplication<AuthApplication>(*args)
}


