package hu.ngykristof.surprise.recommendationcore.service

import hu.ngykristof.surprise.commonscore.util.logger
import hu.ngykristof.surprise.recommendationcore.util.asyncTask
import org.springframework.stereotype.Service
import java.time.OffsetDateTime

@Service
class ETLService {

    val logger = logger()

    //TODO mostantol django EtlService-t kell hivni REST-en o fogja majd futtatni a python scripteket

    fun runSetUpETLFlow() = asyncTask {
        //TODO restClinet hivas a django EtlService app fele (api/start-up)
        logger.debug("Setup ETL flow was started at : ${OffsetDateTime.now()}")
    }

    fun runUpdateMoviesETLFlow() = asyncTask {
        //TODO restClinet hivas a django EtlService app fele (api/update-movies)
        logger.debug("Update movie ETL flow was started at : ${OffsetDateTime.now()}")
    }
}
