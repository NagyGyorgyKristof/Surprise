package hu.ngykristof.surprise.recommendationcore.service

class ETLConstants {

    companion object {
        private const val BASE_URL = "http://localhost:8000/api"
        const val UPDATE_MOVIES_URL = "$BASE_URL/update-movies"
        const val START_UP_URL = "$BASE_URL/start-up"
        const val PROFILE_PARAM_KEY = "profile"
        const val DEBUG_PROFILE = "debug"
        const val PROD_PROFILE = "prod"
    }
}