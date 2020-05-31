package hu.ngykristof.surprise.recommendationcore.repository

import hu.ngykristof.surprise.recommendationcore.data.Movies
import org.springframework.data.neo4j.annotation.Query
import org.springframework.data.neo4j.repository.Neo4jRepository
import org.springframework.stereotype.Repository

@Repository

interface GeneralRepository : Neo4jRepository<Movies, Long> {

    @Query("""
        MATCH (n)
        RETURN n IS NULL AS isEmpty
        LIMIT 1;
    """)
    fun isGraphDatabaseEmpty(): Boolean
}