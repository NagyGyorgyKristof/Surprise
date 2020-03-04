package hu.ngykristof.surprise.commonscore.security

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import hu.ngykristof.surprise.commomconfig.config.jwt.JwtConfig
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.context.SecurityContextHolder
import javax.servlet.FilterChain
import javax.servlet.GenericFilter
import javax.servlet.ServletRequest
import javax.servlet.ServletResponse
import javax.servlet.http.HttpServletRequest

class AuthorizationFilter(
        private val jwtConfig: JwtConfig
) : GenericFilter() {
    override fun doFilter(servletRequest: ServletRequest, servletResponse: ServletResponse, filterChain: FilterChain) {
        val request = servletRequest as HttpServletRequest
        val header = request.getHeader(jwtConfig.header)

        if (header != null && header.startsWith(jwtConfig.tokenPrefix)) {
            val authentication = getAuthentication(request)
            SecurityContextHolder.getContext().authentication = authentication
        }

        filterChain.doFilter(servletRequest, servletResponse)
    }

    private fun getAuthentication(request: HttpServletRequest): UsernamePasswordAuthenticationToken? {
        val token: String? = request.getHeader(jwtConfig.header)

        if (token != null) {
            val jwt = JWT
                    .require(Algorithm.HMAC512(jwtConfig.secret.toByteArray()))
                    .build()
                    .verify(token.replace(jwtConfig.tokenPrefix, ""))

            val username = jwt.getClaim("username").asString()
            val roles = jwt.claims["roles"]?.asArray(String::class.java)
            val authorities = roles?.map { SimpleGrantedAuthority(it) }


            return UsernamePasswordAuthenticationToken(username, null, authorities)
        }
        return null
    }
}

