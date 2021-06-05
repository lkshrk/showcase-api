package harke.me.api.config

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import org.flywaydb.core.Flyway
import org.jetbrains.exposed.sql.Database

object DatabaseFactory {

    fun init(config: DatabaseConfig) {
        Database.connect(hikari(config))
        val flyway = Flyway.configure().dataSource(config.jdbcUrl, config.user, config.password).load()
        flyway.migrate()
    }

    private fun hikari(config: DatabaseConfig): HikariDataSource {
        val hikariConfig = HikariConfig()
        hikariConfig.driverClassName = config.driverClass
        hikariConfig.jdbcUrl = config.jdbcUrl
        hikariConfig.username = config.user
        hikariConfig.password = config.password
        hikariConfig.validate()
        return HikariDataSource(hikariConfig)
    }

}