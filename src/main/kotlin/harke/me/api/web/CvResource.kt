package harke.me.api.web

import harke.me.api.config.withRole
import harke.me.api.model.NewCvEntry
import harke.me.api.service.CvService
import io.ktor.routing.*
import io.ktor.application.*
import io.ktor.http.*
import io.ktor.request.*
import io.ktor.response.*

fun Route.cvRouting(cvService: CvService) {
    route("/cv") {
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
                val cvEntry = call.receive<NewCvEntry>()
                call.respond(HttpStatusCode.Created, cvService.addEntry(cvEntry))
            }
            put {
                val cvEntry = call.receive<NewCvEntry>()
                val updated = cvService.updateEntry(cvEntry)
                call.respond(updated)
            }
            delete("/{id}") {
                val id = call.parameters["id"]?.toInt() ?: throw java.lang.IllegalStateException("Must provide int id")
                cvService.deleteEntry(id)
                call.respond(HttpStatusCode.OK)
            }
        }
    }
}
