package hu.ngykristof.surprise.recommendationcore.service

import hu.ngykristof.surprise.recommendationcore.extensions.executeCommand
import hu.ngykristof.surprise.recommendationcore.util.asyncTask
import org.springframework.stereotype.Service

@Service
class ETLService() {

    companion object {
        private const val PYTHON_CONTAINER_NAME = "surprise-jupyter"
        private const val START_SET_UP_ETL_FLOW_COMMAND: String = "docker exec -it $PYTHON_CONTAINER_NAME /script/setup.py"
        private const val START_UPDATE_MOVIES_ETL_FLOW_COMMAND: String = "docker exec -it $PYTHON_CONTAINER_NAME /script/update_movies.py"
    }

    fun runSetUpETLFlow() = asyncTask {
        START_SET_UP_ETL_FLOW_COMMAND.executeCommand()
    }

    fun runUpdateMoviesETLFlow() = asyncTask {
        START_UPDATE_MOVIES_ETL_FLOW_COMMAND.executeCommand()
    }
}
