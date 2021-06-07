package harke.me.api.service

import harke.me.api.model.Cv
import harke.me.api.persistence.repository.CvRepository
import io.mockk.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.dsl.module
import kotlin.test.*

@OptIn(ExperimentalCoroutinesApi::class)
class CvServiceTest {

    private val cvRepository: CvRepository = mockk()
    private val cut = CvServiceImpl()

    init {
        stopKoin()
        startKoin {
            modules(
                module {
                    single(override = true) { cvRepository }
                }
            )
        }
    }

    @BeforeTest
    fun before() {
        clearMocks(cvRepository)
    }

    @Test
    fun `get all cv entries`() = runBlockingTest {
        val expected = createCvEntry()
        coEvery { cvRepository.getAllEntries() }.returns(listOf(expected))

        val actual = cut.getAllEntries()

        assertContains(actual, expected)
    }

    @Test
    fun `get one cv entry`() = runBlockingTest {
        val expected = createCvEntry()
        coEvery { cvRepository.getEntry(expected.id!!) }.returns(expected)

        val actual = cut.getEntry(expected.id!!)

        assertEquals(actual, expected)
    }

    @Test
    fun `create new cv entry`() = runBlockingTest {
        val expected = createCvEntry()
        val request = Cv(null, "title", "content", 1930, 1950)
        coEvery { cvRepository.create(request) }.returns(expected)

        val actual = cut.addEntry(request)

        assertEquals(actual, expected)
    }

    @Test
    fun `update cv entry is invalid request`() {
        val request = Cv(2, "title", "content", 1930, 1950)
        coJustRun { cvRepository.update(request) }

        assertFailsWith<java.lang.IllegalArgumentException> {
            runBlockingTest {
                cut.updateEntry(1, request)
            }
        }
    }

    @Test
    fun `update cv entry`() = runBlockingTest {
        val expected = createCvEntry()
        val request = Cv(1, "title", "content", 1930, 1950)
        coEvery { cvRepository.update(request) }.returns(expected)

        val actual = cut.updateEntry(request.id!!, request)

        assertEquals(actual, expected)
    }

    @Test
    fun `delete cv entry`() = runBlockingTest {
        coJustRun { cvRepository.delete(1) }

        cut.deleteEntry(1)

        verify {
            runBlockingTest {
                cvRepository.delete(1)
            }
        }
    }

    private fun createCvEntry(): Cv {
        return Cv(
            1,
            "title",
            "some content",
            1990,
            2020,
        )
    }
}