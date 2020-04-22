package hu.ngykristof.surprise.recommendationcore.repository

import hu.ngykristof.surprise.recommendationcore.data.Movies
import org.springframework.data.neo4j.annotation.Query
import org.springframework.data.neo4j.repository.Neo4jRepository

interface SetUpRepository : Neo4jRepository<Movies, Long> {


    @Query("""
                :auto USING PERIODIC COMMIT
        LOAD CSV WITH HEADERS FROM 'file:///movies.csv' AS row
        FIELDTERMINATOR '|'
        CREATE (:Movies {movieId: row.movieId, title: row.title, rating_mean: row.rating_mean});
    """)
    fun uploadMovies()


    @Query("""
        :auto USING PERIODIC COMMIT
        LOAD CSV WITH HEADERS FROM 'file:///genres.csv' AS row
        FIELDTERMINATOR '|'
        CREATE (:Genres {genres: row.genres});
    """)
    fun uploadGenres()


    @Query("""
        :auto USING PERIODIC COMMIT
        LOAD CSV WITH HEADERS FROM 'file:///users.csv' AS row
        FIELDTERMINATOR '|'
        CREATE (:Users {userId: row.userId});
    """)
    fun uploadUsers()

    @Query("""
        CREATE INDEX FOR (n:Movies) ON (n.movieId);
    """)
    fun createIndexForMovies()

    @Query("""
        CREATE INDEX FOR (n:Users) ON (n.userId);
    """)
    fun createIndexForUsers()

    @Query("""
        :auto USING PERIODIC COMMIT
        LOAD CSV WITH HEADERS FROM 'file:///movies_genres.csv' AS row
        FIELDTERMINATOR '|'
        MATCH (movie:Movies {movieId: row.movieId})
        MATCH (genres:Genres {genres: row.genres})
        MERGE (movie)-[:GENRES]->(genres);
    """)
    fun upload_IN_GENRE_Relationship()


    @Query("""
        :auto USING PERIODIC COMMIT
        LOAD CSV WITH HEADERS FROM 'file:///users_movies.csv' AS row
        FIELDTERMINATOR '|'
        MATCH (user:Users {userId: row.userId})
        MATCH (movie:Movies {movieId: row.movieId})
        MERGE (user)-[:WATCHED {rating: row.rating}]->(movie);
    """)
    fun upload_WATCHED_Relationship()
}