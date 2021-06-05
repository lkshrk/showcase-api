package harke.me.api.config

import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.auth.jwt.*
import io.ktor.routing.*
import io.ktor.util.*
import io.ktor.util.pipeline.*


class AuthorizationException(override val message: String): Exception(message)

class RoleBasedAuthorization {

    class Configuration {
    }

    fun interceptPipeline(pipeline: ApplicationCallPipeline, role: String) {
        pipeline.insertPhaseAfter(ApplicationCallPipeline.Features, Authentication.ChallengePhase)
        pipeline.insertPhaseAfter(Authentication.ChallengePhase, AuthorizationPhase)

        pipeline.intercept(AuthorizationPhase) {
            val principal =
                call.authentication.principal<JWTPrincipal>() ?: throw AuthorizationException("Missing principal")
            val principalRole = principal.payload.claims["role"]?.asString()
            if (principalRole != role) {
                throw AuthorizationException("${principal.payload.subject} has no role $role")
            }
        }
    }

    companion object Feature : ApplicationFeature<ApplicationCallPipeline, Configuration, RoleBasedAuthorization> {
        override val key = AttributeKey<RoleBasedAuthorization>("RoleBasedAuthorization")

        val AuthorizationPhase = PipelinePhase("Authorization")

        override fun install(
            pipeline: ApplicationCallPipeline, configure: Configuration.() -> Unit
        ): RoleBasedAuthorization {
            return RoleBasedAuthorization()
        }

    }
}

class AuthorizedRouteSelector(private val description: String) :
    RouteSelector(RouteSelectorEvaluation.qualityConstant) {
    override fun evaluate(context: RoutingResolveContext, segmentIndex: Int) = RouteSelectorEvaluation.Constant

    override fun toString(): String = "(authorize ${description})"
}

fun Route.withRole(role: String, build: Route.() -> Unit): Route {
    val selector = AuthorizedRouteSelector("authorize ($role)")
    val authorizedRoute = createChild(selector)

    val feature = application.feature(RoleBasedAuthorization)
    feature.interceptPipeline(authorizedRoute, role)

    authorizedRoute.build()
    return authorizedRoute
}