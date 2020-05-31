package hu.ngykristof.surprise.recommendationcore.config

import hu.ngykristof.surprise.commonscore.util.logger
import hu.ngykristof.surprise.recommendationcore.repository.GeneralRepository
import hu.ngykristof.surprise.recommendationcore.service.ETLService
import org.springframework.context.annotation.Configuration
import org.springframework.context.event.ContextRefreshedEvent
import org.springframework.context.event.EventListener


@Configuration
class SetUpDbConfiguration(
        private val etlService: ETLService,
        private val generalGraphRepository: GeneralRepository
) {
    val log = logger()

    @EventListener(ContextRefreshedEvent::class)
    fun setUpGraphDatabase() {
        if (generalGraphRepository.isGraphDatabaseEmpty()) {
            log.info("Starting up the neo4j db at the first time")
            etlService.runSetUpETLFlow()
        }
    }
}