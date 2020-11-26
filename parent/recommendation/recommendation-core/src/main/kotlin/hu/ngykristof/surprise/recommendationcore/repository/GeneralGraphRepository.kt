package hu.ngykristof.surprise.recommendationcore.repository

import hu.ngykristof.surprise.recommendationcore.data.Movies
import org.springframework.data.neo4j.annotation.Query
import org.springframework.data.neo4j.repository.Neo4jRepository
import org.springframework.stereotype.Repository

@Repository

interface GeneralGraphRepository : Neo4jRepository<Movies, Long> {

    @Query("""
      MATCH (n)
      RETURN count(n);
    """)
    fun getNodeCount(): Int
}