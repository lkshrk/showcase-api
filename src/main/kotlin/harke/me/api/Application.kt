package harke.me.api

import com.sksamuel.hoplite.ConfigFilePropertySource
import com.sksamuel.hoplite.ConfigLoader
import com.sksamuel.hoplite.ConfigSource
import harke.me.api.config.AppConfig
import harke.me.api.persistence.repository.RepositoryInjection
import harke.me.api.service.ServiceInjection
import io.ktor.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import org.koin.dsl.module
import org.koin.ktor.ext.Koin
import java.io.File

fun main(args: Array<String>) {

    val config: AppConfig = System.getenv()["CONFIG_PATH"]?.let {
        ConfigLoader.Builder()
            .addSource(ConfigFilePropertySource(ConfigSource.FileSource(File(it))))
            .build()
            .loadConfigOrThrow()
    } ?: ConfigLoader().loadConfigOrThrow("/application.yml")


    embeddedServer(Netty, port = config.server.port) {
        module {
            install(Koin) {
                modules(
                    module {
                        single { config }
                    },
                    ServiceInjection.koinBeans,
                    RepositoryInjection.koinBeans
                )
            }
            main()
        }
    }.start(wait = true)
}

fun Application.main() {
    module()
}

