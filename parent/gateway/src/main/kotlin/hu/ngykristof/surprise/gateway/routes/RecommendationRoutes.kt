package hu.ngykristof.surprise.gateway.routes

import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder
import org.springframework.cloud.gateway.route.builder.routes
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration


@Configuration
class RecommendationRoutes {


    @Bean
    fun recomRouteLocator(builder: RouteLocatorBuilder) = builder.routes {
        route(id = "recom") {
            path("/recommendation/**")
            uri("lb://recommendation-service/")
        }
    }
}
