package hu.ngykristof.surprise.userapi.dto

class UpdateUserRequest(
        val firstName: String = "",
        val lastName: String = "",
        val username: String = "",
        val email: String = ""
)