package hu.ngykristof.surprise.recommendationcore.domain

import org.neo4j.ogm.annotation.GeneratedValue
import org.neo4j.ogm.annotation.Id
import org.neo4j.ogm.annotation.NodeEntity
import org.neo4j.ogm.annotation.Relationship

@NodeEntity
class Users(
        @Id
        @GeneratedValue
        var graphId: Long? = null,
        var userId: String = "",
        @Relationship(type = "WATCHED", direction = Relationship.OUTGOING)
        var movies: List<Watched> = emptyList(),
        @Relationship(type = "FAVOURITE_GENRE", direction = Relationship.OUTGOING)
        var genres: List<Genres> = emptyList(),
        @Relationship(type = "FAVOURITE_TAG", direction = Relationship.OUTGOING)
        var tags: List<Tags> = emptyList(),
        @Relationship(type = "SIMILARITY", direction = Relationship.OUTGOING)
        var similarUsers: List<Users> = emptyList()


)

