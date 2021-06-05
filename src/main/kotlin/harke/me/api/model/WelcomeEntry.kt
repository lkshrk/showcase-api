package harke.me.api.model

import kotlinx.serialization.Serializable
import org.jetbrains.exposed.dao.Entity
import org.jetbrains.exposed.dao.EntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IdTable
import org.jetbrains.exposed.sql.Column


object WelcomeEntries : IdTable<String>() {
    override val id: Column<EntityID<String>> = varchar("username", 30).entityId()
    override val primaryKey by lazy { super.primaryKey ?: PrimaryKey(id) }
    val title = WelcomeEntries.varchar("title", 50)
    val coverLetter = WelcomeEntries.varchar("coverLetter", 30000)
}

class WelcomeEntity(id: EntityID<String>) : Entity<String>(id) {
    companion object : EntityClass<String, WelcomeEntity>(WelcomeEntries)

    var title by WelcomeEntries.title
    var coverLetter by WelcomeEntries.coverLetter

    fun toWelcomeEntry() = WelcomeEntry(
        id.value,
        title,
        coverLetter
    )
}

@Serializable
data class WelcomeEntry(val id: String, val title: String, val coverLetter: String)