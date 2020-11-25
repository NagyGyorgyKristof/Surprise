package hu.ngykristof.surprise.recommendationcore.repository;

import hu.ngykristof.surprise.recommendationcore.data.Movies;
import hu.ngykristof.surprise.recommendationcore.data.RecommendationResult;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

//It's required to use java because `$` is reserved in kotlin
@Repository
interface RecommendationRepository : Neo4jRepository<Movies, Long> {


    @Query("""
        MATCH p=(u:Users)-[r:USER_SIMILAR]->(other:Users)-[ow:WATCHED]->(m:Movies)<-[ms:MOVIE_SIMILAR]-(watchedMovie:Movies)<-[wmw:WATCHED]-(u)
                    WHERE u.userId=~${'$'}{userId} AND (NOT (u)-[:WATCHED]->(m))
                    WITH toFloat(m.rating_mean) AS RatingMean,u,m,toFloat(ms.relevance) AS UserSimilarity,toFloat(ow.rating) AS OtherUserRating,toFloat(wmw.rating) AS UserPersonalRating,toFloat(r.similarity) AS MovieSimilarity
                    WITH (RatingMean + 10*UserSimilarity + 10*MovieSimilarity + OtherUserRating + UserPersonalRating)/50 AS RecommendationScore,u,m,RatingMean,UserSimilarity
                    ORDER BY RecommendationScore DESC
                    RETURN DISTINCT toInteger(m.movieId) AS movieId, m.title AS title,toFloat(RatingMean) AS ratingMean 
                    LIMIT 10
    """
    )
    fun hybridRecommendation(@Param("userId") userId: String): List<RecommendationResult>


    @Query("""
        MATCH (m:Movies)<-[ms:MOVIE_SIMILAR]-(watchedMovie:Movies)<-[wmw:WATCHED]-(u)
        WHERE u.userId=~${'$'}{userId} AND (NOT (u)-[:WATCHED]->(m))
        WITH toFloat(m.rating_mean) AS RatingMean,u,m,toFloat(ms.relevance) AS MovieSimilarity,toFloat(wmw.rating) AS UserPersonalRating, watchedMovie
        WITH (10*MovieSimilarity + UserPersonalRating + RatingMean)/30 AS RecommendationScore, u,m,RatingMean,MovieSimilarity,UserPersonalRating,watchedMovie
        ORDER BY RecommendationScore DESC
        RETURN DISTINCT toInteger(m.movieId) AS movieId, m.title AS title,toFloat(RatingMean) AS ratingMean 
        LIMIT 10
    """
    )
    fun contentBasedRecommendation(@Param("userId") userId: String): List<RecommendationResult>

    @Query("""
         MATCH p=(u:Users)-[r:USER_SIMILAR]->(other:Users)-[ow:WATCHED]->(m:Movies)
         WHERE u.userId=~${'$'}{userId} AND (NOT (u)-[:WATCHED]->(m))
         WITH toFloat(m.rating_mean) AS RatingMean,u,m,r.similarity AS UserSimilarity,  toFloat(ow.rating) AS UserPersonalRating
         WITH (10*UserSimilarity + UserPersonalRating + RatingMean)/30 AS RecommendationScore,m,RatingMean
         ORDER BY RecommendationScore DESC
         RETURN DISTINCT toInteger(m.movieId) AS movieId, m.title AS title,toFloat(RatingMean) AS ratingMean 
         LIMIT 10
    """
    )
    fun userBasedRecommendation(@Param("userId") userId: String): List<RecommendationResult>


    @Query("""
        MATCH (user:Users {userId: ${'$'}{userId}})
        MATCH (movie:Movies {movieId: ${'$'}{movieId}})
        MERGE (user)-[:WATCHED {rating: ${'$'}{rating}}]->(movie);
    """
    )
    fun createRating(userId: String, movieId: String, rating: Double)
}
