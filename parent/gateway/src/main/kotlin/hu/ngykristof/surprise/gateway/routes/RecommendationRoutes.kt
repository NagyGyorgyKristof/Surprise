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
class RecommendationRoutes(
    private val webClientBuilder: WebClient.Builder,
    @Value("\${surprise.validate-access-token-url}")
    private val accessTokenUrl: String
) {


    @Bean
    fun recomRouteLocator(builder: RouteLocatorBuilder) = builder.routes {
        route(id = "recom") {
            path("/recommendation/**")
            filters {
                this.filter(AuthenticatedRequestFilter(webClientBuilder, accessTokenUrl).apply(Any()))
            }
            uri("lb://recommendation-service/")
        }
    }
}
