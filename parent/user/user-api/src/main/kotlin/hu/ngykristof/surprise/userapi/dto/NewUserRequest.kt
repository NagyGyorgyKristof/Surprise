package hu.ngykristof.surprise.userapi.dto

class NewUserRequest(
        val firstName: String = "",
        val lastName: String = "",
        val username: String = "",
        val password: String = "",
        val email: String = "",
        val isAdmin: Boolean
)
