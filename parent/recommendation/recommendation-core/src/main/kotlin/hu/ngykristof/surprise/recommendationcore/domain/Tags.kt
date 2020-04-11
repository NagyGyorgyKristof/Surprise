package hu.ngykristof.surprise.recommendationcore.domain

import org.neo4j.ogm.annotation.GeneratedValue
import org.neo4j.ogm.annotation.Id
import org.neo4j.ogm.annotation.NodeEntity
import org.neo4j.ogm.annotation.Relationship

@NodeEntity
class Tags(
        @Id
        @GeneratedValue
        var graphId: Long? = null,
        var tagId: String,
        var tag: String,
        @Relationship(type = "FAVOURITE_TAG", direction = Relationship.INCOMING)
        var relatedUsers: List<Users> = emptyList(),
        @Relationship(type = "TAGGED_WITH", direction = Relationship.INCOMING)
        var relatedMovies: List<Movies> = emptyList()
)
