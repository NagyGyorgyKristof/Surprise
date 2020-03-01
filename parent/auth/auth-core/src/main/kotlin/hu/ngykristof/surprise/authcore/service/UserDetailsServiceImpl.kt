package hu.ngykristof.surprise.authcore.service

import hu.ngykristof.surprise.commonscore.config.jwt.JwtConfig
import org.springframework.security.core.authority.AuthorityUtils
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import java.util.*


@Service
class UserDetailsServiceImpl(
        val encoder: PasswordEncoder,
        val jwtConfig: JwtConfig
) : UserDetailsService {


    override fun loadUserByUsername(username: String): UserDetails {

        // hard coding the users. All passwords must be encoded.
        // TODO bekötni a userService-t és onnan lekérdezni a usereket
        val users: List<AppUser> = Arrays.asList(
                AppUser(1, "omar", encoder.encode("12345"), "USER"),
                AppUser(2, "admin", encoder.encode("12345"), "ADMIN")
        )

        for (appUser in users) {
            if (appUser.userName == username) {
                val grantedAuthorities = AuthorityUtils
                        .commaSeparatedStringToAuthorityList(jwtConfig.rolePrefix + appUser.role)
                return User(appUser.userName, appUser.password, grantedAuthorities)
            }
        }

        throw UsernameNotFoundException("Username: $username not found")
    }

    //TODO ha lesz user service akkor nem kell ez
    data class AppUser(
            val id: Int,
            val userName: String,
            val password: String,
            val role: String

    )
}



