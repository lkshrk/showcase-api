package harke.me.api.persistence

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import harke.me.api.config.AppConfig
import kotlinx.coroutines.ObsoleteCoroutinesApi
import kotlinx.coroutines.newFixedThreadPoolContext
import kotlinx.coroutines.withContext
import org.flywaydb.core.Flyway
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.transactions.transaction
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import kotlin.coroutines.CoroutineContext

@OptIn(ObsoleteCoroutinesApi::class)
class DatabaseProvider: DatabaseProviderContract, KoinComponent {

    private val config by inject<AppConfig>()
    private val dispatcher: CoroutineContext

    init {
        dispatcher = newFixedThreadPoolContext(5, "database-pool")
    }

    override fun init() {
        Database.connect(hikari())
        migrate()
    }

    private fun hikari(): HikariDataSource {
        HikariConfig().run {
            driverClassName = config.database.driverClass
            jdbcUrl = config.database.jdbcUrl
            username = config.database.user
            password = config.database.password
            isAutoCommit = false
            transactionIsolation = "TRANSACTION_REPEATABLE_READ"
            validate()
            return HikariDataSource(this)
        }
    }

    private fun migrate() {
        val flyway = Flyway.configure().dataSource(config.database.jdbcUrl, config.database.user, config.database.password).load()
        flyway.migrate()
    }

    override suspend fun <T> dbQuery(block: () -> T): T = withContext(dispatcher) {
        transaction { block() }
    }

}

interface DatabaseProviderContract {
    fun init()
    suspend fun <T> dbQuery(block: () -> T): T
}