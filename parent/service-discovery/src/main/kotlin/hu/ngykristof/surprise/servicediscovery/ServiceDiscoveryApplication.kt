package hu.ngykristof.surprise.servicediscovery

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer

@SpringBootApplication
@EnableEurekaServer
class ServiceDiscoveryApplication

fun main(args: Array<String>) {
	runApplication<ServiceDiscoveryApplication>(*args)
}
