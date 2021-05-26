package db.migration

import harke.me.api.model.CvEntries
import harke.me.api.model.WelcomeEntries
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