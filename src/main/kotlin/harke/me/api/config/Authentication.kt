package harke.me.api.config

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import io.ktor.auth.*
import io.ktor.auth.jwt.*

fun Authentication.Configuration.authenticationConfig(config: AuthConfig) {

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