package hu.ngykristof.surprise.usercore.repository

import hu.ngykristof.surprise.usercore.domain.UserEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface UserRepository : JpaRepository<UserEntity, String> {

    fun findOneByUsername(username: String): UserEntity?

    fun findOneByEmailIgnoreCase(email: String): UserEntity?

    fun findOneByActivationKey(activationKey: String): UserEntity?
}