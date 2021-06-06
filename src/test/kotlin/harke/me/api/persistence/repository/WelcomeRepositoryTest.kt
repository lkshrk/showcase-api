package harke.me.api.persistence.repository

import harke.me.api.config.DatabaseConfig
import harke.me.api.model.Welcome
import harke.me.api.persistence.initDatabase
import harke.me.api.persistence.model.WelcomeEntity
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.assertj.core.api.Assertions.assertThat
import org.flywaydb.core.Flyway
import org.jetbrains.exposed.dao.exceptions.EntityNotFoundException
import org.jetbrains.exposed.sql.transactions.transaction
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.assertThrows

@OptIn(ExperimentalCoroutinesApi::class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class WelcomeRepositoryTest {

    private var cut = WelcomeRepositoryImpl()
    private val config = DatabaseConfig("org.h2.Driver", "jdbc:h2:mem:test", null, null)
    private val flyway = Flyway.configure().dataSource(config.jdbcUrl, config.user, config.password).load()

    init {
        initDatabase(config)
    }

    @BeforeEach
    fun clearDatabase() {
        flyway.clean()
        flyway.migrate()
    }

    @Test
    fun `save welcome entry`() = runBlockingTest {
        val request = Welcome("user", "title", "some text")
        val actual = cut.create(request)

        transaction {
            assertThat(WelcomeEntity[actual.id]).isNotNull
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

        assertThat(actual).isEqualTo(expected)
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

        transaction {
            assertThat(WelcomeEntity[entry.id]).satisfies {
                assertThat(it.id.value).isEqualTo(updateWelcome.id)
                assertThat(it.title).isEqualTo(updateWelcome.title)
                assertThat(it.coverLetter).isEqualTo(updateWelcome.coverLetter)
            }
        }
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

        transaction {
            assertThrows<EntityNotFoundException> {
                runBlockingTest {
                    WelcomeEntity[entry.id]
                }
            }
        }
    }
}