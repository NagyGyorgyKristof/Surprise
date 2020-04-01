package hu.ngykristof.surprise.gateway.routes

import hu.ngykristof.surprise.gateway.filter.AuthenticatedRequestFilter
import org.springframework.beans.factory.annotation.Value
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder
import org.springframework.cloud.gateway.route.builder.filters
import org.springframework.cloud.gateway.route.builder.routes
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.reactive.function.client.WebClient

@Configuration
class AuthRoutes(
        private val webClientBuilder: WebClient.Builder,
        @Value("\${surprise.validate-access-token-url}")
        private val accessTokenUrl: String
) {

    @Bean
    fun authRouteLocator(builder: RouteLocatorBuilder) = builder.routes {
        route(id = "logout") {
            path("/auth/me/logout")
            filters {
                this.filter(AuthenticatedRequestFilter(webClientBuilder, accessTokenUrl).apply(Any()))
            }
            uri("lb://auth-service/")
        }

        route(id = "auth-service-login") {
            path("/auth/**")
            uri("lb://auth-service/")
        }
    }
}
