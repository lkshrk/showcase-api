package harke.me.api.persistence

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import harke.me.api.config.AppConfig
import harke.me.api.config.DatabaseConfig
import kotlinx.coroutines.ObsoleteCoroutinesApi
import kotlinx.coroutines.newFixedThreadPoolContext
import kotlinx.coroutines.withContext
import org.flywaydb.core.Flyway
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.transactions.transaction
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import kotlin.coroutines.CoroutineContext



fun initDatabase(config: DatabaseConfig) {
    Database.connect(hikari(config))
    migrate(config)
}

private fun hikari(config: DatabaseConfig): HikariDataSource {
    HikariConfig().run {
        driverClassName = config.driverClass
        jdbcUrl = config.jdbcUrl
        username = config.user
        password = config.password
        isAutoCommit = false
        transactionIsolation = "TRANSACTION_REPEATABLE_READ"
        validate()
        return HikariDataSource(this)
    }
}

private fun migrate(config: DatabaseConfig) {
    val flyway = Flyway.configure().dataSource(config.jdbcUrl, config.user, config.password).load()
    flyway.migrate()
}
