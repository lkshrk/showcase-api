package harke.me.api

import harke.me.api.config.*
import harke.me.api.persistence.DatabaseProviderContract
import harke.me.api.web.cvRouting
import harke.me.api.web.welcomeRouting
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.features.*
import io.ktor.http.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.serialization.*
import kotlinx.serialization.json.Json
import org.jetbrains.exposed.dao.exceptions.EntityNotFoundException
import org.koin.ktor.ext.inject

fun Application.module() {

    val databaseProvider by inject<DatabaseProviderContract>()
    val config by inject<AppConfig>()

    databaseProvider.init()

    install(DefaultHeaders)
    install(ContentNegotiation) {
        json(Json{prettyPrint = true})
    }
    install(StatusPages) {
        exception<EntityNotFoundException> {
            call.respond(HttpStatusCode.NotFound)
        }
        exception<AuthorizationException> {
            call.respond(HttpStatusCode.Unauthorized)
        }
        exception<IllegalArgumentException> {
            call.respond(HttpStatusCode.BadRequest)
        }
    }

    install(Authentication) {
        authenticationConfig(config.auth)
    }

    install(RoleBasedAuthorization)

    install(Routing) {
        authenticate("auth-jwt") {
            cvRouting()
            welcomeRouting()
        }
    }
}