package harke.me.api.web

import harke.me.api.config.withRole
import harke.me.api.service.WelcomeService
import harke.me.api.model.Welcome
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.auth.jwt.*
import io.ktor.http.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import org.koin.ktor.ext.inject

fun Route.welcomeRouting() {

    val welcomeService by inject<WelcomeService>()

    route("/welcome") {
        authenticate("auth-jwt") {
            get {
                val principal = call.authentication.principal<JWTPrincipal>()?.payload?.subject
                if (principal != null) {
                    val cvEntry = welcomeService.getEntry(principal)
                    call.respond(cvEntry)
                }
            }
            withRole("admin") {
                post {
                    val welcomeEntry = call.receive<Welcome>()
                    call.respond(HttpStatusCode.Created, welcomeService.addEntry(welcomeEntry))
                }
                put("/{id}") {
                    val id = call.parameters["id"] ?: throw java.lang.IllegalStateException("Must provide id")
                    val welcomeEntry = call.receive<Welcome>()
                    val updated = welcomeService.updateEntry(id, welcomeEntry)
                    call.respond(updated)
                }
                delete("/{id}") {
                    val id = call.parameters["id"] ?: throw java.lang.IllegalStateException("Must provide id")
                    welcomeService.deleteEntry(id)
                    call.respond(HttpStatusCode.OK)
                }
            }
        }
    }
}