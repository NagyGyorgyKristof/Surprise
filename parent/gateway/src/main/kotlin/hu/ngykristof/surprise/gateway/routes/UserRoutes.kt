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
class UserRoutes(
        private val webClientBuilder: WebClient.Builder,
        @Value("\${surprise.validate-access-token-url}")
        private val accessTokenUrl: String
) {

    @Bean
    fun userRouteLocator(builder: RouteLocatorBuilder) = builder.routes {
        route(id = "user-service-auth") {
            path("/users/register")
            uri("lb://user-service/")
        }

        route(id = "user-service-core") {
            path("/users/details/**")
            filters {
                this.filter(AuthenticatedRequestFilter(webClientBuilder, accessTokenUrl).apply(Any()))
            }
            uri("lb://user-service/")
        }
    }
}