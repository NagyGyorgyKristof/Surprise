package hu.ngykristof.surprise.authcore.task

import hu.ngykristof.surprise.authcore.service.AuthService
import hu.ngykristof.surprise.commonscore.util.logger
import org.springframework.context.annotation.Configuration
import org.springframework.scheduling.annotation.EnableScheduling
import org.springframework.scheduling.annotation.Scheduled
import java.time.OffsetDateTime

@Configuration
@EnableScheduling
class ExpiredTokensCleanupTask(
        private val authService: AuthService
) {

    private val log = logger()

    @Scheduled(cron = "\${dprototypefirst.auth.token.cleanup-frequency-cron-expression}")
    fun cleanExpiredTokens() {
        log.info("The expired tokens were deleted in UTC at: ${OffsetDateTime.now()} ")
        authService.cleanExpiredTokens()
    }
}
