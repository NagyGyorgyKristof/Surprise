package hu.ngykristof.surprise.recommendationcore.domain

import org.neo4j.ogm.annotation.GeneratedValue
import org.neo4j.ogm.annotation.Id
import org.neo4j.ogm.annotation.NodeEntity
import org.neo4j.ogm.annotation.Relationship


@NodeEntity
data class Movies(
        @Id
        @GeneratedValue
        var graphId: Long? = null,
        var movieId: String = "",
        var title: String = "",
        var ratingMean: Double = 0.0,
        @Relationship(type = "IN_GENRE", direction = Relationship.OUTGOING)
        var relatedGenres: List<Genres> = emptyList(),
        @Relationship(type = "TAGGED_WITH", direction = Relationship.OUTGOING)
        var relatedTags: List<Tags> = emptyList(),
        @Relationship(type = "WATCHED", direction = Relationship.INCOMING)
        var ratings: List<Watched> = emptyList()
)

