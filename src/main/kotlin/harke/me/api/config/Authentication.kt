package harke.me.api.config

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.auth.jwt.*

fun Application.configureAuth(config: AuthConfig) {

    install(Authentication) {
        jwt("auth-jwt") {
            verifier(JWT
                .require(Algorithm.HMAC256(config.secret))
                .withIssuer(config.issuer)
                .build())
            validate { credentials ->
                if (credentials.payload.subject != null) {
                    JWTPrincipal(credentials.payload)
                } else {
                    null
                }
            }
        }
    }
}