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
                    "WHERE u.userId=~$userId AND (NOT (u)-[:WATCHED]->(m)) AND toFloat(ow.rating)>7.0 AND toFloat(wmw.rating)>7.0 AND toFloat(ms.relevance)> 0.3 AND  toFloat(r.similarity)> 0.5" + "\n" +
                    "WITH toFloat(m.rating_mean) AS RatingMean,u,m,ms.relevance AS RELEVANCE,watchedMovie" + "\n" +
                    "RETURN DISTINCT u.userId, m.title AS title, RatingMean AS ratingMean, RELEVANCE, watchedMovie.title,  toInteger(m.movieId) AS movieId" + "\n" +
                    "ORDER BY ratingMean DESC" + "\n" +
                    "LIMIT 10"
    )
    List<RecommendationResult> hybridRecommendation(@Param("userId") String userId);


    @Query(
            "MATCH (m:Movies)<-[ms:MOVIE_SIMILAR]-(watchedMovie:Movies)<-[wmw:WATCHED]-(u)" + "\n" +
                    "WHERE u.userId=~$userId AND (NOT (u)-[:WATCHED]->(m)) AND toFloat(wmw.rating)>7.0 AND toFloat(ms.relevance) > 0.4" + "\n" +
                    "WITH toFloat(m.rating_mean) AS RatingMean,u,m,ms.relevance AS RELEVANCE,wmw.rating AS WMWRATING, watchedMovie" + "\n" +
                    "RETURN DISTINCT u.userId, m.title AS title, RatingMean AS ratingMean, toInteger(m.movieId) AS movieId" + "\n" +
                    "ORDER BY ratingMean DESC" + "\n" +
                    "LIMIT 10"
    )
    List<RecommendationResult> contentBasedRecommendation(@Param("userId") String userId);

    @Query(
            "MATCH p=(u:Users)-[r:USER_SIMILAR]->(other:Users)-[ow:WATCHED]->(m:Movies)" + "\n" +
                    "WHERE u.userId=~$userId AND (NOT (u)-[:WATCHED]->(m)) AND toFloat(ow.rating)>7.0 AND toFloat(r.similarity)> 0.6" + "\n" +
                    "WITH toFloat(m.rating_mean) AS RatingMean,u,m,r.similarity AS RELEVANCE" + "\n" +
                    "RETURN DISTINCT m.title AS title, RatingMean AS ratingMean,  toInteger(m.movieId) AS movieId" + "\n" +
                    "ORDER BY ratingMean DESC" + "\n" +
                    "LIMIT 10"
    )
    List<RecommendationResult> userBasedRecommendation(@Param("userId") String userId);
}
