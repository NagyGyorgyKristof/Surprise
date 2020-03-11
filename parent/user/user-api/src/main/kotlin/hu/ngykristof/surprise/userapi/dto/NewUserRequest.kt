package hu.ngykristof.surprise.userapi.dto

import hu.ngykristof.surprise.userapi.dto.loginvalidation.Role

class NewUserRequest(
        val firstName: String = "",
        val lastName: String = "",
        val username: String = "",
        val password: String = "",
        val email: String = "",
        val roles:List<Role>
)
