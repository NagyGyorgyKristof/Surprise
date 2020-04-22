package hu.ngykristof.surprise.recommendationcore.repository;

import org.springframework.stereotype.Repository;
//It's required to use java because `$` is reserved in kotlin
//TODO ezt megnez rendesn
@Repository
public interface RecommendationRepository{
//
//
//    @Query(
//            "MATCH (u:Users)-[r:SIMILARITY]->(other:Users)-[ow:WATCHED]->(m:Movies)," +
//                    "(m)-[:IN_GENRE]->(g:Genres)," +
//                    "(u)-[fg:FAVOURITE_GENRE]->(g)" +
//                    "WHERE u.userId=~$userId AND (NOT (u)-[:WATCHED]->(m)) AND toFloat(ow.rating) > 4.4 " +
//                    "WITH toFloat(m.rating_mean) AS RatingMean,u,m,count(DISTINCT fg) AS FGenreCount" + "\n" +
//                    "WITH RatingMean,u,m,FGenreCount AS ProfileScore" + "\n" +
//                    "WITH RatingMean,u,m,ProfileScore, 5 AS MaxRating" + "\n" +
//                    "WITH RatingMean,u,m,ProfileScore,ProfileScore/MaxRating AS NormalizationFactor" + "\n" +
//                    "WITH RatingMean,u,m,ProfileScore,NormalizationFactor, ((RatingMean*NormalizationFactor*0.6)+(ProfileScore*0.4)) AS RecommendationScore" + "\n" +
//                    "RETURN DISTINCT u.userId AS userId, m.title AS title, RatingMean AS ratingMean,RecommendationScore AS recommendationScore" + "\n" +
//                    "ORDER BY RecommendationScore DESC" + "\n" +
//                    "LIMIT 5"
//    )
//    List<UserBasedRecommendation> userBasedCollaborativeFilteringRecommendation(@Param("userId") String userId);
//
//
//    @Query(
//            "MATCH (m)-[:IN_GENRE]->(g:Genres)<-[:IN_GENRE]-(other)" + "\n" +
//                    "WHERE m.title CONTAINS($movieTitle)" + "\n" +
//
//                    "WITH m, other, count(g) AS intersection, collect(g.genres) AS i" + "\n" +
//                    "MATCH(m)-[:IN_GENRE]->(mg:Genres)" + "\n" +
//                    "WITH m,other, intersection,i, collect(mg.genres) AS s1" + "\n" +
//                    "MATCH (other)-[:IN_GENRE]->(og:Genres)" + "\n" +
//                    "WITH m,other,intersection,i, s1, collect(og.genres) AS s2" + "\n" +
//
//                    "WITH m,other,intersection,s1,s2" + "\n" +
//
//                    "WITH m,other,intersection,s1+[x IN s2 WHERE NOT x IN s1] AS union, s1, s2" + "\n" +
//
//                    "WITH m,other,((1.0*intersection)/size(union)) AS jaccard" + "\n" +
//
//                    "WHERE toFloat(m.rating_mean) >4.0" +
//                    "RETURN DISTINCT m.title, other.title,jaccard" + "\n" +
//                    "ORDER BY jaccard DESC LIMIT 10"
//    )
//    List<ContentBasedRecommendation> contentBasedRecommendation(@Param("movieTitle") String movieTitle);
}
