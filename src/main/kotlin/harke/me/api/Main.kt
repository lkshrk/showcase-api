package harke.me.api

import harke.me.api.config.AuthorizationException
import harke.me.api.config.DatabaseFactory
import harke.me.api.config.RoleBasedAuthorization
import harke.me.api.config.configureAuth
import harke.me.api.service.CvService
import harke.me.api.service.WelcomeService
import harke.me.api.web.cvRouting
import harke.me.api.web.welcomeRouting
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.features.*
import io.ktor.http.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.serialization.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.util.*
import kotlinx.serialization.json.Json
import org.jetbrains.exposed.dao.exceptions.EntityNotFoundException

fun main(args: Array<String>) { embeddedServer(Netty, commandLineEnvironment(args)).start(wait = true) }

@KtorExperimentalAPI
@Suppress("unsued") // referenced in Application.conf
fun Application.module() {
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
    }
    configureAuth()
    install(RoleBasedAuthorization)


    // db init
    DatabaseFactory.init()

    // services & routes
    val cvService = CvService()
    val welcomeService = WelcomeService()
    install(Routing) {
        authenticate("auth-jwt") {
            cvRouting(cvService)
            welcomeRouting(welcomeService)
        }
    }
}


