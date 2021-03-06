    package hu.ngykristof.surprise.usercore

import hu.ngykristof.surprise.commonscore.config.jwt.EnableJwtConfig
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.cloud.client.discovery.EnableDiscoveryClient
import org.springframework.context.annotation.ComponentScan

@SpringBootApplication
@EnableDiscoveryClient
@ComponentScan("hu.ngykristof.surprise")
@EnableJwtConfig
class UserCoreApplication

fun main(args: Array<String>) {
    runApplication<UserCoreApplication>(*args)
}
