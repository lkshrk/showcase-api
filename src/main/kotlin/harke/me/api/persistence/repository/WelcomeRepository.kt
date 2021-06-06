package harke.me.api.persistence.repository

import harke.me.api.persistence.model.WelcomeEntity
import harke.me.api.model.Welcome
import org.jetbrains.exposed.sql.transactions.transaction
import org.koin.core.component.KoinComponent

class WelcomeRepositoryImpl: WelcomeRepository, KoinComponent {

    override suspend fun getEntry(id: String): Welcome = transaction {
        WelcomeEntity[id].toWelcome()
    }
    override suspend fun create(welcome: Welcome): Welcome = transaction {
        WelcomeEntity.new(welcome.id) {
            this.title = welcome.title
            this.coverLetter = welcome.coverLetter
        }.toWelcome()
    }
    override suspend fun update(welcome: Welcome): Welcome = transaction {
        WelcomeEntity[welcome.id].also {
            it.title = welcome.title
            it.coverLetter = welcome.coverLetter
        }.toWelcome()
    }

    override suspend fun delete(id: String) = transaction {
        WelcomeEntity[id].delete()
    }
}

interface WelcomeRepository {
    suspend fun getEntry(id: String): Welcome
    suspend fun create(welcome: Welcome): Welcome
    suspend fun update(welcome: Welcome): Welcome
    suspend fun delete(id: String)
}