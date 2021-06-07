package harke.me.api.persistence.repository

import harke.me.api.config.DatabaseConfig
import harke.me.api.model.Cv
import harke.me.api.persistence.initDatabase
import harke.me.api.persistence.model.CvEntity
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.flywaydb.core.Flyway
import org.jetbrains.exposed.dao.exceptions.EntityNotFoundException
import org.jetbrains.exposed.sql.transactions.transaction
import org.junit.jupiter.api.TestInstance
import kotlin.test.*

@OptIn(ExperimentalCoroutinesApi::class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class CvRepositoryTest {

    private var cut = CvRepositoryImpl()
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
    fun `save cv entry`() = runBlockingTest {
        val request = Cv(null, "title", "some content", 1999, 2010)
        val actual = cut.create(request)

        transaction {
            assertNotNull(CvEntity[actual.id!!])
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

        assertContentEquals(actual, expected)
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

        assertEquals(actual, expected)
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

        val actual = transaction {
            CvEntity[entry.id]
        }
        assertEquals(actual.id.value, updateCv.id)
        assertEquals(actual.title, updateCv.title)
        assertEquals(actual.content, updateCv.content)
        assertEquals(actual.startYear, updateCv.startYear)
        assertEquals(actual.endYear, updateCv.endYear)
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

        assertFailsWith<EntityNotFoundException> {
            runBlockingTest {
                transaction {
                    CvEntity[entry.id]
                }
            }
        }
    }
}