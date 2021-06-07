package harke.me.api.persistence.repository

import harke.me.api.config.DatabaseConfig
import harke.me.api.model.Welcome
import harke.me.api.persistence.initDatabase
import harke.me.api.persistence.model.WelcomeEntity
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.flywaydb.core.Flyway
import org.jetbrains.exposed.dao.exceptions.EntityNotFoundException
import org.jetbrains.exposed.sql.transactions.transaction
import kotlin.test.*

@OptIn(ExperimentalCoroutinesApi::class)
class WelcomeRepositoryTest {

    private var cut = WelcomeRepositoryImpl()
    private val config = DatabaseConfig("org.h2.Driver", "jdbc:h2:mem:test", null, null)
    private val flyway = Flyway.configure().dataSource(config.jdbcUrl, config.user, config.password).load()

    init {
        initDatabase(config)
    }

    @BeforeTest
    fun clearDatabase() {
        flyway.clean()
        flyway.migrate()
    }

    @Test
    fun `save welcome entry`() = runBlockingTest {
        val request = Welcome("user", "title", "some text")
        val actual = cut.create(request)

        transaction {
            assertNotNull(WelcomeEntity[actual.id])
        }
    }

    @Test
    fun `get single welcome entry`() = runBlockingTest {
        val entry = transaction {
            WelcomeEntity.new("user") {
                this.title = "title"
                this.coverLetter = "some text"
            }
        }

        val expected = entry.toWelcome()
        val actual = cut.getEntry(entry.id.value)

        assertEquals(actual, expected)
    }

    @Test
    fun `update welcome entry`() = runBlockingTest {
        val entry = transaction {
            WelcomeEntity.new("the_user") {
                this.title = "title"
                this.coverLetter = "text lorem ipsum"
            }
        }

        val updateWelcome = Welcome(entry.id.value, "new title", "other content")
        cut.update(updateWelcome)

        val actual = transaction {
            WelcomeEntity[entry.id]
        }
        assertEquals(actual.id.value, updateWelcome.id)
        assertEquals(actual.title, updateWelcome.title)
        assertEquals(actual.coverLetter, updateWelcome.coverLetter)
    }

    @Test
    fun `delete welcome entry`() = runBlockingTest {
        val entry = transaction {
            WelcomeEntity.new("more user") {
                this.title = "title"
                this.coverLetter = "more cover letter"
            }
        }
        cut.delete(entry.id.value)

        assertFailsWith<EntityNotFoundException> {
            runBlockingTest {
                transaction {
                    WelcomeEntity[entry.id]
                }
            }
        }
    }
}