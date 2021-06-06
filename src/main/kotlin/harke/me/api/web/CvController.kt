package harke.me.api.web

import harke.me.api.config.withRole
import harke.me.api.service.CvService
import harke.me.api.model.Cv
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.http.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import org.koin.ktor.ext.inject

fun Route.cvRouting() {

    val cvService by inject<CvService>()

    route("/cv") {
        authenticate("auth-jwt") {
            get {
                call.respond(cvService.getAllEntries())
            }
            get("{id}") {
                val id = call.parameters["id"]?.toInt() ?: throw  IllegalStateException("Must provide int id")
                val cvEntry = cvService.getEntry(id)
                call.respond(cvEntry)
            }
            withRole("admin") {
                post {
                    val cvEntry = call.receive<Cv>()
                    call.respond(HttpStatusCode.Created, cvService.addEntry(cvEntry))
                }
                put("{id}") {
                    val id = call.parameters["id"]?.toInt() ?: throw  IllegalStateException("Must provide int id")
                    val cvEntry = call.receive<Cv>()
                    val updated = cvService.updateEntry(id, cvEntry)
                    call.respond(updated)
                }
                delete("/{id}") {
                    val id =
                        call.parameters["id"]?.toInt() ?: throw java.lang.IllegalStateException("Must provide int id")
                    cvService.deleteEntry(id)
                    call.respond(HttpStatusCode.OK)
                }
            }
        }
    }
}
