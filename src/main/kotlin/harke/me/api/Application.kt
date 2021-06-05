package harke.me.api

import com.sksamuel.hoplite.ConfigLoader
import harke.me.api.config.AppConfig
import harke.me.api.persistence.DatabaseProvider
import harke.me.api.persistence.DatabaseProviderContract
import harke.me.api.service.ServiceInjection
import io.ktor.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import org.koin.dsl.module
import org.koin.ktor.ext.Koin

fun main(args: Array<String>) {

    val configPath = System.getenv()["CONFIG_PATH"] ?: "/application.yml"
    val config = ConfigLoader().loadConfigOrThrow<AppConfig>(configPath)

    embeddedServer(Netty, port = config.server.port) {
        module {
            install(Koin) {
                modules(
                    module {
                        single { config }
                        single<DatabaseProviderContract> { DatabaseProvider() }
                    },
                    ServiceInjection.koinBeans
                )
            }
            main()
        }
    }.start(wait = true)
}

fun Application.main() {
    module()
}

