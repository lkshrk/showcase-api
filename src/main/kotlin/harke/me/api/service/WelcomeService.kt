package harke.me.api.service

import harke.me.api.model.WelcomeEntity
import harke.me.api.model.WelcomeEntry
import org.jetbrains.exposed.sql.transactions.transaction

class WelcomeService {

    fun getEntry(principal: String): WelcomeEntry = transaction {
        WelcomeEntity[principal].toWelcomeEntry()
    }

    fun addEntry(welcomeEntry: WelcomeEntry): WelcomeEntry = transaction {
        WelcomeEntity.new(welcomeEntry.id) {
            this.title = welcomeEntry.title
            this.coverLetter = welcomeEntry.coverLetter
        }.toWelcomeEntry()
    }

    fun deleteEntry(id: String) {
        WelcomeEntity[id].delete()
    }

}