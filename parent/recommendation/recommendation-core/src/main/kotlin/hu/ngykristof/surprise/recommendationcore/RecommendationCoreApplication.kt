package hu.ngykristof.surprise.recommendationcore

import hu.ngykristof.surprise.commonscore.config.jwt.EnableJwtConfig
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.cloud.client.discovery.EnableDiscoveryClient
import org.springframework.context.annotation.ComponentScan

@SpringBootApplication
@EnableDiscoveryClient
@ComponentScan("hu.ngykristof.surprise")
@EnableJwtConfig
class RecommendationCoreApplication

fun main(args: Array<String>) {
	runApplication<RecommendationCoreApplication>(*args)
}
