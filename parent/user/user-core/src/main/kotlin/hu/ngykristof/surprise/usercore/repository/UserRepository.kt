package hu.ngykristof.surprise.usercore.repository

import hu.ngykristof.surprise.usercore.domain.UserEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface UserRepository : JpaRepository<UserEntity, String> {

    fun findByUsername(username: String): UserEntity?
}