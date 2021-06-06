package harke.me.api.persistence.repository

import harke.me.api.model.Cv
import harke.me.api.persistence.model.CvEntity
import org.jetbrains.exposed.sql.transactions.transaction
import org.koin.core.component.KoinComponent

class CvRepositoryImpl: CvRepository, KoinComponent {
    

    override suspend fun getAllEntries(): Iterable<Cv> = transaction {
        CvEntity.all().map(CvEntity::toCv)
    }
    override suspend fun getEntry(id: Int): Cv = transaction {
        CvEntity[id].toCv()
    }
    override suspend fun create(cv: Cv): Cv = transaction {
        CvEntity.new {
            this.title = cv.title
            this.content = cv.content
            this.startYear = cv.startYear
            this.endYear = cv.endYear
            this.dateUpdated = System.currentTimeMillis()
        }.toCv()
    }
    override suspend fun update(cv: Cv): Cv = transaction {
        CvEntity[cv.id!!].also {
            it.title = cv.title
            it.content = cv.content
            it.startYear = cv.startYear
            it.endYear = cv.endYear
            it.dateUpdated = System.currentTimeMillis()
        }.toCv()
    }

    override suspend fun delete(id: Int) = transaction {
        CvEntity[id].delete()
    }
}

interface CvRepository {
    suspend fun getAllEntries(): Iterable<Cv>
    suspend fun getEntry(id: Int): Cv
    suspend fun create(cv: Cv): Cv
    suspend fun update(cv: Cv): Cv
    suspend fun delete(id: Int)
}