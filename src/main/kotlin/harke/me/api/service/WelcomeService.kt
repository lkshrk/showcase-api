package harke.me.api.service

import harke.me.api.persistence.DatabaseProviderContract
import harke.me.api.persistence.WelcomeEntity
import harke.me.api.web.model.WelcomeBody
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class WelcomeServiceImpl: WelcomeService, KoinComponent {

    private val dbProvider by inject<DatabaseProviderContract>()

    override suspend fun getEntry(principal: String): WelcomeBody = dbProvider.dbQuery {
        WelcomeEntity[principal].toWelcomeEntry()
    }

    override suspend fun addEntry(welcomeEntry: WelcomeBody): WelcomeBody = dbProvider.dbQuery {
        WelcomeEntity.new(welcomeEntry.id) {
            this.title = welcomeEntry.title
            this.coverLetter = welcomeEntry.coverLetter
        }.toWelcomeEntry()
    }

    override suspend fun deleteEntry(id: String) = dbProvider.dbQuery {
        WelcomeEntity[id].delete()
    }
}

interface WelcomeService {
    suspend fun getEntry(principal: String): WelcomeBody
    suspend fun addEntry(welcomeEntry: WelcomeBody): WelcomeBody
    suspend fun deleteEntry(id: String)
}