package harke.me.api.service

import harke.me.api.persistence.DatabaseProviderContract
import harke.me.api.persistence.CvEntryEntity
import harke.me.api.web.model.CvBody
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class CvServiceImpl: CvService, KoinComponent {

    private val dbProvider by inject<DatabaseProviderContract>()

    override suspend fun getAllEntries(): Iterable<CvBody> = dbProvider.dbQuery {
        CvEntryEntity.all().map(CvEntryEntity::toCvEntry)
    }

    override suspend fun getEntry(id: Int): CvBody = dbProvider.dbQuery {
        CvEntryEntity[id].toCvEntry()
    }

    override suspend fun updateEntry(cvEntry: CvBody): CvBody {
        return if (cvEntry.id != null) {
            val entry = CvEntryEntity[cvEntry.id]
            entry.title = cvEntry.title
            entry.content = cvEntry.content
            entry.startYear = cvEntry.startYear
            entry.endYear = cvEntry.endYear
            entry.dateUpdated = System.currentTimeMillis()
            entry.toCvEntry()
        } else {
            addEntry(cvEntry)
        }
    }

    override suspend fun addEntry(cvEntry: CvBody): CvBody = dbProvider.dbQuery {
        CvEntryEntity.new {
            this.title = cvEntry.title
            this.content = cvEntry.content
            this.startYear = cvEntry.startYear
            this.endYear = cvEntry.endYear
            this.dateUpdated = System.currentTimeMillis()
        }.toCvEntry()
    }

    override suspend fun deleteEntry(id: Int) = dbProvider.dbQuery {
        CvEntryEntity[id].delete()
    }
}

interface CvService {
    suspend fun getAllEntries(): Iterable<CvBody>
    suspend fun getEntry(id: Int): CvBody
    suspend fun updateEntry(cvEntry: CvBody) : CvBody
    suspend fun addEntry(cvEntry: CvBody): CvBody
    suspend fun deleteEntry(id: Int)
}