package harke.me.api.service

import harke.me.api.model.Cv
import harke.me.api.persistence.repository.CvRepository
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class CvServiceImpl: CvService, KoinComponent {

    private val cvRepository by inject<CvRepository>()

    override suspend fun getAllEntries(): Iterable<Cv> {
        return cvRepository.getAllEntries()
    }

    override suspend fun getEntry(id: Int): Cv {
        return cvRepository.getEntry(id)
    }

    override suspend fun updateEntry(id: Int, cv: Cv): Cv {
        if (id != cv.id) throw IllegalArgumentException()
        return cvRepository.update(cv)
    }

    override suspend fun addEntry(cv: Cv): Cv {
        return cvRepository.create(cv)
    }

    override suspend fun deleteEntry(id: Int) {
        return cvRepository.delete(id)
    }
}

interface CvService {
    suspend fun getAllEntries(): Iterable<Cv>
    suspend fun getEntry(id: Int): Cv
    suspend fun updateEntry(id: Int, cv: Cv) : Cv
    suspend fun addEntry(cv: Cv): Cv
    suspend fun deleteEntry(id: Int)
}