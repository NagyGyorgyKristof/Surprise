package hu.ngykristof.surprise.gateway.routes

import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder
import org.springframework.cloud.gateway.route.builder.routes
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class AuthRoutes {

    @Bean
    fun authRouteLocator(builder: RouteLocatorBuilder) = builder.routes {
        route(id = "auth-service-login") {
            path("/auth/login")
            uri("lb://auth-service/")
        }
    }
}