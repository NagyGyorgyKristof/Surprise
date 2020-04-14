package hu.ngykristof.surprise.recommendationcore.service

import hu.ngykristof.surprise.commonscore.util.logger
import hu.ngykristof.surprise.recommendationcore.repository.SetUpRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional


@Service
open class SetUpService(
        private val setUpRepository: SetUpRepository
) {

    val log = logger()

    @Transactional
    open fun setUpBaseDB() {
        try {
            uploadNodeEntities()
            createIndexes()
            uploadBaseRelationships()
            log.info("Graph database set up was successful")
        } catch (e: Exception) {
            log.error("Error occurred during graph database setup with error: ${e.message}")
        }
    }


    @Transactional
    open fun uploadNodeEntities() = applySetUpRepository {
        uploadMovies()
        uploadTags()
        uploadGenres()
        uploadUsers()
    }

    @Transactional
    open fun createIndexes() = applySetUpRepository {
        createIndexForMovies()
        createIndexForTags()
        createIndexForUsers()
    }

    @Transactional
    open fun uploadBaseRelationships() = applySetUpRepository {
        upload_IN_GENRE_Relationship()
        upload_TAGGED_WITH_Relationship()
        upload_WATCHED_Relationship()
    }

    private fun applySetUpRepository(block: SetUpRepository.() -> Unit) {
        setUpRepository.block()
    }
}
