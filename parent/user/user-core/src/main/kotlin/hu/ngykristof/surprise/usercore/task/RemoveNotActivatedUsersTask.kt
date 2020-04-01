package hu.ngykristof.surprise.usercore.task

import hu.ngykristof.surprise.commonscore.util.logger
import hu.ngykristof.surprise.usercore.service.UserService
import org.springframework.context.annotation.Configuration
import org.springframework.scheduling.annotation.EnableScheduling
import org.springframework.scheduling.annotation.Scheduled

@Configuration
@EnableScheduling
class RemoveNotActivatedUsersTask(
        private val userService: UserService
) {

    val log = logger()

    @Scheduled(cron = "\${dprototypefirst.auth.users.cleanup-frequency-cron-expression}")
    fun removeNotActivatedUsers() {
        log.info("Deleting not activated users")
        userService.removeNotActivatedUsers()
    }
}
