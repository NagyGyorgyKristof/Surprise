package hu.ngykristof.surprise.recommendationcore.repository;

import hu.ngykristof.surprise.recommendationcore.data.Movies;
import hu.ngykristof.surprise.recommendationcore.data.query.RecommendationResult;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

//It's required to use java because `$` is reserved in kotlin
@Repository
public interface RecommendationRepository extends Neo4jRepository<Movies, Long> {


    @Query(
            "MATCH p=(u:Users)-[r:USER_SIMILAR]->(other:Users)-[ow:WATCHED]->(m:Movies)<-[ms:MOVIE_SIMILAR]-(watchedMovie:Movies)<-[wmw:WATCHED]-(u)" + "\n" +
                    "WHERE u.userId=~$userId AND (NOT (u)-[:WATCHED]->(m))" + "\n" +
                    "WITH toFloat(m.rating_mean) AS RatingMean,u,m,toFloat(ms.relevance) AS UserSimilarity,toFloat(ow.rating) AS OtherUserRating,toFloat(wmw.rating) AS UserPersonalRating,toFloat(r.similarity) AS MovieSimilarity" + "\n" +
                    "WITH (RatingMean + 10*UserSimilarity + 10*MovieSimilarity + OtherUserRating + UserPersonalRating)/50 AS RecommendationScore,u,m,RatingMean,UserSimilarity" + "\n" +
                    "ORDER BY RecommendationScore DESC" + "\n" +
                    "RETURN DISTINCT toInteger(m.movieId) AS movieId, m.title AS title,toFloat(RatingMean) AS ratingMean " + "\n" +
                    "LIMIT 10"
    )
    List<RecommendationResult> hybridRecommendation(@Param("userId") String userId);


    @Query(
            "MATCH (m:Movies)<-[ms:MOVIE_SIMILAR]-(watchedMovie:Movies)<-[wmw:WATCHED]-(u)" + "\n" +
                    "WHERE u.userId=~$userId AND (NOT (u)-[:WATCHED]->(m))" + "\n" +
                    "WITH toFloat(m.rating_mean) AS RatingMean,u,m,toFloat(ms.relevance) AS MovieSimilarity,toFloat(wmw.rating) AS UserPersonalRating, watchedMovie" + "\n" +
                    "WITH (10*MovieSimilarity + UserPersonalRating + RatingMean)/30 AS RecommendationScore, u,m,RatingMean,MovieSimilarity,UserPersonalRating,watchedMovie" + "\n" +
                    "ORDER BY RecommendationScore DESC" + "\n" +
                    "RETURN DISTINCT toInteger(m.movieId) AS movieId, m.title AS title,toFloat(RatingMean) AS ratingMean " + "\n" +
                    "LIMIT 10"
    )
    List<RecommendationResult> contentBasedRecommendation(@Param("userId") String userId);

    @Query(
            "MATCH p=(u:Users)-[r:USER_SIMILAR]->(other:Users)-[ow:WATCHED]->(m:Movies)" + "\n" +
                    "WHERE u.userId=~$userId AND (NOT (u)-[:WATCHED]->(m))" + "\n" +
                    "WITH toFloat(m.rating_mean) AS RatingMean,u,m,r.similarity AS MovieSimilarity,  toFloat(ow.rating) AS UserPersonalRating" + "\n" +
                    "WITH (10*MovieSimilarity + UserPersonalRating + RatingMean)/30 AS RecommendationScore,m,RatingMean" + "\n" +
                    "ORDER BY RecommendationScore DESC" + "\n" +
                    "RETURN DISTINCT toInteger(m.movieId) AS movieId, m.title AS title,toFloat(RatingMean) AS ratingMean " + "\n" +
                    "LIMIT 10"
    )
    List<RecommendationResult> userBasedRecommendation(@Param("userId") String userId);
}
