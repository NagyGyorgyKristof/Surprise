package hu.ngykristof.surprise.recommendationcore.config

import hu.ngykristof.surprise.commonscore.util.logger
import hu.ngykristof.surprise.recommendationcore.service.EtlService
import hu.ngykristof.surprise.recommendationcore.service.GeneralGraphService
import org.springframework.context.annotation.Configuration
import org.springframework.context.event.ContextRefreshedEvent
import org.springframework.context.event.EventListener


@Configuration
class SetUpDbConfiguration(
        private val etlService: EtlService,
        private val generalGraphService: GeneralGraphService
) {
    val log = logger()

    @EventListener(ContextRefreshedEvent::class)
    fun setUpGraphDatabase() {
        if (generalGraphService.isGraphEmpty()) {
            log.info("Starting up the neo4j db at the first time")
            etlService.runSetupETLFlow()
        }
    }
}