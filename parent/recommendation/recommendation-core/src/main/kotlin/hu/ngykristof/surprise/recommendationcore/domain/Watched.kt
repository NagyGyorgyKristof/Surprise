package hu.ngykristof.surprise.recommendationcore.domain

import org.neo4j.ogm.annotation.*

@RelationshipEntity(type = "WATCHED")
class Watched(
        @Id
        @GeneratedValue
        var graphId: Long? = null,
        @StartNode
        var users: Users,
        @EndNode
        var movie: Movies,
        val rating: String = ""
)

