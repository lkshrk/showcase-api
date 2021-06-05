package harke.me.api.config

data class AppConfig(
    val server: ServerConfig,
    val database: DatabaseConfig,
    val auth: AuthConfig
)

data class DatabaseConfig(
    val driverClass: String,
    val jdbcUrl: String,
    val user: String?,
    val password: String?
)

data class AuthConfig(
    val secret: String,
    val issuer: String
)

data class ServerConfig(
    val port: Int
)