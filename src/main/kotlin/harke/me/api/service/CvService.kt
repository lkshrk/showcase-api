package harke.me.api.service

import harke.me.api.model.CvEntryEntity
import harke.me.api.model.CvEntry
import harke.me.api.model.NewCvEntry
import org.jetbrains.exposed.sql.transactions.transaction


class CvService {


    fun getAllEntries(): Iterable<CvEntry> = transaction {
        CvEntryEntity.all().map(CvEntryEntity::toCvEntry)
    }

    fun getEntry(id: Int): CvEntry = transaction {
        CvEntryEntity[id].toCvEntry()
    }

    fun updateEntry(cvEntry: NewCvEntry) : CvEntry = transaction {
        if (cvEntry.id != null) {
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

    fun addEntry(cvEntry: NewCvEntry): CvEntry = transaction {
        CvEntryEntity.new {
            this.title = cvEntry.title
            this.content = cvEntry.content
            this.startYear = cvEntry.startYear
            this.endYear = cvEntry.endYear
            this.dateUpdated = System.currentTimeMillis()
        }.toCvEntry()
    }

    fun deleteEntry(id: Int) = transaction {
        CvEntryEntity[id].delete()
    }
}