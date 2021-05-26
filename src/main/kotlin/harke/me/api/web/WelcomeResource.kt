package harke.me.api.web

import harke.me.api.config.withRole
import harke.me.api.model.WelcomeEntry
import harke.me.api.service.WelcomeService
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.auth.jwt.*
import io.ktor.http.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*

fun Route.welcomeRouting(welcomeService: WelcomeService) {
    route("/welcome") {
        get{
            val principal = call.authentication.principal<JWTPrincipal>()?.payload?.subject
            if (principal != null) {
                val cvEntry = welcomeService.getEntry(principal)
                call.respond(cvEntry)
            }
        }
        withRole("admin") {
            post {
                val welcomeEntry = call.receive<WelcomeEntry>()
                call.respond(HttpStatusCode.Created, welcomeService.addEntry(welcomeEntry))
            }
            delete("/{id}") {
                val id = call.parameters["id"] ?: throw java.lang.IllegalStateException("Must provide int id")
                welcomeService.deleteEntry(id)
                call.respond(HttpStatusCode.OK)
            }
        }
    }
}