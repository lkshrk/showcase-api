package harke.me.api.persistence

import harke.me.api.web.model.CvBody
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable


object CvEntries : IntIdTable("CV") {
    val title = CvEntries.varchar("title", 50)
    val content = CvEntries.varchar("content", 1000)
    val startYear = CvEntries.integer("startYear")
    val endYear = CvEntries.integer("endYear")
    val dateUpdated = long("dateUpdated")
}

class CvEntryEntity(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<CvEntryEntity>(CvEntries)

    var title by CvEntries.title
    var content by CvEntries.content
    var startYear by CvEntries.startYear
    var endYear by CvEntries.endYear
    var dateUpdated by CvEntries.dateUpdated

    fun toCvEntry() = CvBody(
        id.value,
        title,
        content,
        startYear,
        endYear
    )
}

