package hu.ngykristof.surprise.usercore.domain

import hu.ngykristof.surprise.commonscore.domain.BaseEntity
import javax.persistence.ElementCollection
import javax.persistence.Entity
import javax.persistence.Inheritance
import javax.persistence.InheritanceType


@Entity
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
data class UserEntity(
        var firstName: String = "",
        var lastName: String = "",
        var username: String = "",
        var password: String = "",
        var email: String = "",
        var activationKey: String = "",
        var isActive: Boolean = false,
        @ElementCollection(targetClass = RoleEntity::class)
        var roles: List<RoleEntity> = emptyList()
) : BaseEntity()

enum class RoleEntity {
    USER, ADMIN
}