package harke.me.api.web

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import harke.me.api.config.AuthConfig
import harke.me.api.config.RoleBasedAuthorization
import harke.me.api.config.authenticationConfig
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.features.*
import io.ktor.routing.*
import io.ktor.serialization.*
import io.ktor.server.testing.*
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import org.koin.core.context.stopKoin
import org.koin.ktor.ext.Koin
import org.koin.core.module.Module
import java.sql.Timestamp
import java.time.LocalDateTime
import java.util.*

abstract class ControllerTestBase {

    protected var koinModules: Module? = null

    private val authConfig = AuthConfig(
        "secret",
        "issuer"
    )

    init {
        stopKoin()
    }

    fun <R> withControllerTestApplication(test: TestApplicationEngine.() -> R) {
        withTestApplication({
            install(ContentNegotiation) { json(Json { prettyPrint = true }) }
            koinModules?.let {
                install(Koin) {
                    modules(it)
                }
            }
            install(Authentication) {
                authenticationConfig(authConfig)
            }
            install(RoleBasedAuthorization)
            install(Routing) {
                cvRouting()
                welcomeRouting()
            }

        }) {
            test()
        }
    }

    fun createTokenWithRole(role: String): String {
        return JWT.create()
            .withExpiresAt(Timestamp.valueOf(LocalDateTime.now().plusDays(1)))
            .withIssuer(authConfig.issuer)
            .withSubject("someuser")
            .withClaim("role", role)
            .sign(Algorithm.HMAC256(authConfig.secret))
    }

}