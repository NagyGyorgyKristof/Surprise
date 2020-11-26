package hu.ngykristof.surprise.recommendationcore.data

import org.neo4j.ogm.annotation.GeneratedValue
import org.neo4j.ogm.annotation.Id
import org.neo4j.ogm.annotation.NodeEntity
import org.neo4j.ogm.annotation.Relationship

@NodeEntity
class Genres(
        @Id
        @GeneratedValue
        var graphId: Long? = null,
        var genres: String = "",
        @Relationship(type = "FAVOURITE_GENRE", direction = Relationship.INCOMING)
        var relatedUsers: List<Users> = emptyList(),
        @Relationship(type = "IN_GENRE", direction = Relationship.INCOMING)
        var relatedMovies: List<Movies> = emptyList()
)
