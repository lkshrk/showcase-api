package harke.me.api

import harke.me.api.config.AppConfig
import harke.me.api.config.AuthorizationException
import harke.me.api.config.RoleBasedAuthorization
import harke.me.api.config.authenticationConfig
import harke.me.api.persistence.initDatabase
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

    val config by inject<AppConfig>()

    initDatabase(config.database)

    install(DefaultHeaders)

    install(XForwardedHeaderSupport)
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
        cvRouting()
        welcomeRouting()
    }
}