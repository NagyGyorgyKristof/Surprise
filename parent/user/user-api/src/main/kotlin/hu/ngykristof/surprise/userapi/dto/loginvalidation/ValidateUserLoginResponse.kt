package hu.ngykristof.surprise.userapi.dto.loginvalidation

class ValidateUserLoginResponse(
        val username: String = "",
        var userId: String = "",
        var roles: List<Role> = emptyList()
)

enum class Role {
    USER, ADMIN
}