package harke.me.api.config

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.typesafe.config.ConfigFactory
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.auth.jwt.*
import io.ktor.config.*

fun Application.configureAuth() {
    val appConfig = HoconApplicationConfig(ConfigFactory.load())
    val jwtSecret = appConfig.property("jwt.secret").getString()
    val jwtIssuer = appConfig.property("jwt.issuer").getString()


    install(Authentication) {
        jwt("auth-jwt") {
            verifier(JWT
                .require(Algorithm.HMAC256(jwtSecret))
                .withIssuer(jwtIssuer)
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