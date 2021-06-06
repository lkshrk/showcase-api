package harke.me.api.service

import harke.me.api.persistence.repository.WelcomeRepository
import harke.me.api.model.Welcome
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class WelcomeServiceImpl: WelcomeService, KoinComponent {

    private val welcomeRepository by inject<WelcomeRepository>()

    override suspend fun getEntry(principal: String): Welcome {
        return welcomeRepository.getEntry(principal)
    }

    override suspend fun addEntry(welcome: Welcome): Welcome {
        return welcomeRepository.create(welcome)
    }

    override suspend fun updateEntry(id: String, welcome: Welcome): Welcome {
        if (id != welcome.id) throw IllegalArgumentException()
        return welcomeRepository.update(welcome)
    }

    override suspend fun deleteEntry(id: String) {
        welcomeRepository.delete(id)
    }
}

interface WelcomeService {
    suspend fun getEntry(principal: String): Welcome
    suspend fun addEntry(welcome: Welcome): Welcome
    suspend fun updateEntry(id: String, welcome: Welcome): Welcome
    suspend fun deleteEntry(id: String)
}