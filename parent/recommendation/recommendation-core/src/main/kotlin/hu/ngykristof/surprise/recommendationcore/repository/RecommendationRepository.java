package hu.ngykristof.surprise.recommendationcore.repository;

import hu.ngykristof.surprise.recommendationcore.domain.Movies;
import hu.ngykristof.surprise.recommendationcore.domain.query.RecommendedMovie;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

//It's required to use java because `$` is reserved in kotlin
@Repository
public interface RecommendationRepository extends Neo4jRepository<Movies, Long> {


    @Query(
            //User-based Collaborative Filter recommendation
            "MATCH (u:Users)-[r:SIMILARITY]->(other:Users)-[ow:WATCHED]->(m:Movies)," +
                    "(m)-[:IN_GENRE]->(g:Genres)," +
                    "(u)-[fg:FAVOURITE_GENRE]->(g)," +
                    "(m)-[:TAGGED_WITH]->(t:Tags)," +
                    "(u)-[ft:FAVOURITE_TAG]->(t)" +
                    "WHERE u.userId=~$userId AND (NOT (u)-[:WATCHED]->(m)) AND toFloat(ow.rating) > 4.4 " +
                    "WITH toFloat(m.rating_mean) AS RatingMean,u,m,count(DISTINCT fg) AS FGenreCount, count(DISTINCT ft) AS FTagCount" + "\n" +
                    "WITH RatingMean,u,m,(FGenreCount+FTagCount) AS ProfileScore" + "\n" +
                    "WITH RatingMean,u,m,ProfileScore, 5 AS MaxRating" + "\n" +
                    "WITH RatingMean,u,m,ProfileScore,ProfileScore/MaxRating AS NormalizationFactor" + "\n" +
                    "WITH RatingMean,u,m,ProfileScore,NormalizationFactor, ((RatingMean*NormalizationFactor*0.6)+(ProfileScore*0.4)) AS RecommendationScore" + "\n" +
                    "RETURN DISTINCT u.userId AS userId, m.title AS title, RatingMean AS ratingMean,RecommendationScore AS recommendationScore" + "\n" +
                    "ORDER BY RecommendationScore DESC" + "\n" +
                    "LIMIT 5"
    )
    List<RecommendedMovie> userBasedCollaborativeFilterRecommendation(@Param("userId") String userId);
}
