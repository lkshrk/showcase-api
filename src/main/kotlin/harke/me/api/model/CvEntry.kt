package harke.me.api.model

import kotlinx.serialization.Serializable
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable


object CvEntries : IntIdTable() {
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

    fun toCvEntry() = CvEntry(
        id.value,
        title,
        content,
        startYear,
        endYear
    )
}

@Serializable
data class CvEntry(val id: Int, val title: String, val content: String, val startYear: Int, val endYear: Int)

@Serializable
data class NewCvEntry(val id: Int? = null, val title: String, val content: String, val startYear: Int, val endYear: Int)