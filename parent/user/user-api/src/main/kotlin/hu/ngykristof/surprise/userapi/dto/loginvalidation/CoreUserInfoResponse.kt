package hu.ngykristof.surprise.userapi.dto.loginvalidation

class CoreUserInfoResponse(
        val username: String = "",
        var userId: String = "",
        var roles: List<Role> = emptyList()
)
