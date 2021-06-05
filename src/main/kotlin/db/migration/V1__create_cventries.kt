package db.migration

import harke.me.api.persistence.CvEntries
import harke.me.api.persistence.WelcomeEntries
import org.flywaydb.core.api.migration.BaseJavaMigration
import org.flywaydb.core.api.migration.Context
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction

@Suppress("unused")
class V1__create_basetables: BaseJavaMigration() {
    override fun migrate(context: Context?) {
        transaction {
            SchemaUtils.create(CvEntries)
            SchemaUtils.create(WelcomeEntries)
        }
    }
}