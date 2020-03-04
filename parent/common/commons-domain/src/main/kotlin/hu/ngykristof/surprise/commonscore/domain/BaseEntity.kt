package hu.ngykristof.surprise.commonscore.domain

import org.hibernate.annotations.GenericGenerator
import java.time.OffsetDateTime
import javax.persistence.*

@MappedSuperclass
abstract class BaseEntity {

    @Id
    @GenericGenerator(name = "uuid2", strategy = "uuid2", parameters = [])
    @GeneratedValue(generator = "uuid2")
    @Column(length = 36)
    val id: String? = null

    private var created: OffsetDateTime? = null

    private var lastModified: OffsetDateTime? = null

    @PrePersist
    fun onPrePersist() {
        created = OffsetDateTime.now()
    }

    @PreUpdate
    fun onPreUpdate() {
        lastModified = OffsetDateTime.now()
    }
}