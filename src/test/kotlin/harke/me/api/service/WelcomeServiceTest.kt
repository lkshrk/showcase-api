package harke.me.api.service

import harke.me.api.model.Welcome
import harke.me.api.persistence.repository.WelcomeRepository
import io.mockk.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.dsl.module
import kotlin.test.assertFailsWith

@OptIn(ExperimentalCoroutinesApi::class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class WelcomeServiceTest {

    private val welcomeRepository: WelcomeRepository = mockk()
    private val cut = WelcomeServiceImpl()

    init {
        stopKoin()
        startKoin {
            modules(
                module {
                    single(override = true) { welcomeRepository }
                }
            )
        }
    }

    @BeforeEach
    fun before() {
        clearMocks(welcomeRepository)
    }

    @Test
    fun `get one welcome entry`() = runBlockingTest {
        val expected = createWelcome()
        coEvery { welcomeRepository.getEntry(expected.id) }.returns(expected)

        val actual = cut.getEntry(expected.id)

        assertThat(actual).isEqualTo(expected)
    }

    @Test
    fun `create new welcome entry`() = runBlockingTest {
        val expected = createWelcome()
        val request = Welcome("user", "title", "text")
        coEvery { welcomeRepository.create(request) }.returns(expected)

        val actual = cut.addEntry(request)

        assertThat(actual).isEqualTo(expected)
    }

    @Test
    fun `update welcome entry is invalid request`() {
        val request = Welcome("user", "title", "content")
        coJustRun { welcomeRepository.update(request) }

        assertFailsWith<java.lang.IllegalArgumentException> {
            runBlockingTest {
                cut.updateEntry("someOne", request)
            }
        }
    }

    @Test
    fun `update welcome entry`() = runBlockingTest {
        val expected = createWelcome()
        val request = Welcome("user", "title", "content")
        coEvery { welcomeRepository.update(request) }.returns(expected)

        val actual = cut.updateEntry(request.id, request)

        assertThat(actual).isEqualTo(expected)
    }

    @Test
    fun `delete welcome entry`() = runBlockingTest {
        val id = "user"
        coJustRun { welcomeRepository.delete(id) }

        cut.deleteEntry(id)

        verify {
            runBlockingTest {
                welcomeRepository.delete(id)
            }
        }
    }

    private fun createWelcome(): Welcome {
        return Welcome(
            "user",
            "title",
            "some text",
        )
    }
}