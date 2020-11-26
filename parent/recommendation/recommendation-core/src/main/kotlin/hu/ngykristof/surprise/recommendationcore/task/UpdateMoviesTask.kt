package hu.ngykristof.surprise.recommendationcore.task

import hu.ngykristof.surprise.commonscore.util.logger
import hu.ngykristof.surprise.recommendationcore.service.ETLService
import org.springframework.scheduling.annotation.EnableScheduling
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import java.time.OffsetDateTime

@Component
@EnableScheduling
class UpdateMoviesTask(
        private val etlService: ETLService
) {

    val log = logger()

    @Scheduled(cron = "\${surprise.task.movie-update-frequency-cron-expression}")
    fun removeNotActivatedUsers() {
        log.info("Running update movies ETL flow at : ${OffsetDateTime.now()}")
        etlService.runUpdateMoviesETLFlow()
    }

}