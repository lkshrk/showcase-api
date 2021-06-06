package harke.me.api.web

import harke.me.api.config.AuthorizationException
import harke.me.api.service.CvService
import harke.me.api.model.Cv
import io.ktor.http.*
import io.ktor.server.testing.*
import io.mockk.clearMocks
import io.mockk.coEvery
import io.mockk.coJustRun
import io.mockk.mockk
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.*
import org.koin.dsl.module

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class CvControllerTest: ControllerTestBase() {

    private val cvService: CvService = mockk()

    @BeforeAll
    fun setup() {
        koinModules = module {
            single(override = true) { cvService }
        }

    }

    @BeforeEach
    fun clearMocks() {
        clearMocks(cvService)
    }

    @Test
    fun `query cv entries with missing auth returns unauthorized`() = withControllerTestApplication {
        coEvery { cvService.getAllEntries() }.returns(emptyList())

        val call = handleRequest(HttpMethod.Get, "/cv")
        with(call) {
            assertThat(response.status()).isEqualTo(HttpStatusCode.Unauthorized)
        }
    }

    @Test
    fun `query all cv entries`() = withControllerTestApplication {
        val expected = listOf(createCvBody(1))
        coEvery { cvService.getAllEntries() }.returns(expected)

        val call = handleRequest(HttpMethod.Get, "/cv") {
            addHeader(HttpHeaders.Authorization, "Bearer "+createTokenWithRole("role"))
        }
        with(call) {
            val actual = response.content?.let {
                Json.decodeFromString<List<Cv>>(it)
            }
            assertThat(response.status()).isEqualTo(HttpStatusCode.OK)
            assertThat(actual).containsExactlyElementsOf(expected)
        }
    }

    @Test
    fun `query single cv entry`() = withControllerTestApplication {
        val expected = createCvBody(1)
        coEvery { cvService.getEntry(1) }.returns(expected)

        val call = handleRequest(HttpMethod.Get, "/cv/1") {
            addHeader(HttpHeaders.Authorization, "Bearer "+createTokenWithRole("role"))
        }
        with(call) {
            val actual = response.content?.let {
                Json.decodeFromString<Cv>(it)
            }
            assertThat(response.status()).isEqualTo(HttpStatusCode.OK)
            assertThat(actual).isEqualTo(expected)
        }
    }

    @Test
    fun `create cv entry without admin role`() = withControllerTestApplication {

        val body = Json.encodeToString(createCvBody(null))
        assertThrows<AuthorizationException> {
            handleRequest(HttpMethod.Post, "/cv") {
                addHeader(HttpHeaders.Authorization, "Bearer "+createTokenWithRole("role"))
                addHeader(HttpHeaders.ContentType, ContentType.Application.Json.toString())
                setBody(body)
            }
        }
    }

    @Test
    fun `create cv entry`() = withControllerTestApplication {

        val request = createCvBody(null)
        val body = Json.encodeToString(request)
        val expected = createCvBody(1)

        coEvery { cvService.addEntry(request) }.returns(expected)

        val call = handleRequest(HttpMethod.Post, "/cv") {
            addHeader(HttpHeaders.Authorization, "Bearer "+createTokenWithRole("admin"))
            addHeader(HttpHeaders.ContentType, ContentType.Application.Json.toString())
            setBody(body)
        }
        with(call) {
            val actual = response.content?.let {
                Json.decodeFromString<Cv>(it)
            }
            assertThat(response.status()).isEqualTo(HttpStatusCode.Created)
            assertThat(actual).isEqualTo(expected)
        }
    }

    @Test
    fun `update cv entry`() = withControllerTestApplication {

        val request = createCvBody(1)
        val body = Json.encodeToString(request)

        coEvery { cvService.updateEntry(request.id!!, request) }.returns(request)

        val call = handleRequest(HttpMethod.Put, "/cv/"+request.id) {
            addHeader(HttpHeaders.Authorization, "Bearer "+createTokenWithRole("admin"))
            addHeader(HttpHeaders.ContentType, ContentType.Application.Json.toString())
            setBody(body)
        }
        with(call) {
            val actual = response.content?.let {
                Json.decodeFromString<Cv>(it)
            }
            assertThat(response.status()).isEqualTo(HttpStatusCode.OK)
            assertThat(actual).isEqualTo(request)
        }
    }

    @Test
    fun `delete cv entry`() = withControllerTestApplication {

        val id = 1
        coJustRun { cvService.deleteEntry(id) }

        val call = handleRequest(HttpMethod.Delete, "/cv/$id") {
            addHeader(HttpHeaders.Authorization, "Bearer "+createTokenWithRole("admin"))
        }
        with(call) {
            assertThat(response.status()).isEqualTo(HttpStatusCode.OK)
        }
    }

    private fun createCvBody(id: Int?): Cv {
        return Cv(id, "someTitle", "some content", 2020, 2030)
    }
}