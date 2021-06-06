package harke.me.api.persistence.repository

import harke.me.api.config.DatabaseConfig
import harke.me.api.model.Cv
import harke.me.api.persistence.initDatabase
import harke.me.api.persistence.model.CvEntity
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
class CvRepositoryTest {

    private var cut = CvRepositoryImpl()
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
    fun `save cv entry`() = runBlockingTest {
        val request = Cv(null, "title", "some content", 1999, 2010)
        val actual = cut.create(request)

        transaction {
            assertThat(CvEntity[actual.id!!]).isNotNull
        }
    }

    @Test
    fun `get all cv entries`() = runBlockingTest {
        val entries = transaction {
            listOf(
                CvEntity.new {
                    this.title = "title"
                    this.content = "content"
                    this.startYear = 1980
                    this.endYear = 2020
                    this.dateUpdated = System.currentTimeMillis()
                },
                CvEntity.new {
                    this.title = "some title"
                    this.content = "content"
                    this.startYear = 2000
                    this.endYear = 2010
                    this.dateUpdated = System.currentTimeMillis()
                }
            )
        }

        val expected = entries.map(CvEntity::toCv)
        val actual = cut.getAllEntries()

        assertThat(actual).containsExactlyElementsOf(expected)
    }

    @Test
    fun `get single cv entry`() = runBlockingTest {
        val entry = transaction {
            CvEntity.new {
                this.title = "title"
                this.content = "content"
                this.startYear = 1980
                this.endYear = 2020
                this.dateUpdated = System.currentTimeMillis()
            }
        }

        val expected = entry.toCv()
        val actual = cut.getEntry(entry.id.value)

        assertThat(actual).isEqualTo(expected)
    }

    @Test
    fun `update cv entry`() = runBlockingTest {
        val entry = transaction {
            CvEntity.new {
                this.title = "title"
                this.content = "content"
                this.startYear = 1980
                this.endYear = 2020
                this.dateUpdated = System.currentTimeMillis()
            }
        }

        val updateCv = Cv(entry.id.value, "new title", "other content", 1980, 2020)
        cut.update(updateCv)

        transaction {
            assertThat(CvEntity[entry.id]).satisfies {
                assertThat(it.id.value).isEqualTo(updateCv.id)
                assertThat(it.title).isEqualTo(updateCv.title)
                assertThat(it.content).isEqualTo(updateCv.content)
                assertThat(it.startYear).isEqualTo(updateCv.startYear)
                assertThat(it.endYear).isEqualTo(updateCv.endYear)
            }
        }
    }

    @Test
    fun `delete cv entry`() = runBlockingTest {
        val entry = transaction {
            CvEntity.new {
                this.title = "title"
                this.content = "content"
                this.startYear = 1980
                this.endYear = 2020
                this.dateUpdated = System.currentTimeMillis()
            }
        }
        cut.delete(entry.id.value)

        transaction {
            assertThrows<EntityNotFoundException> {
                runBlockingTest {
                    CvEntity[entry.id]
                }
            }
        }
    }
}