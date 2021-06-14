package harke.me.api.web

import harke.me.api.config.AuthorizationException
import harke.me.api.model.Welcome
import harke.me.api.service.WelcomeService
import io.ktor.http.*
import io.ktor.server.testing.*
import io.mockk.clearMocks
import io.mockk.coEvery
import io.mockk.coJustRun
import io.mockk.mockk
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.koin.dsl.module
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class WelcomeControllerTest: ControllerTestBase() {

    private val welcomeService: WelcomeService = mockk()

    init {
        koinModules = module {
            single() { welcomeService }
        }
    }

    @BeforeTest
    fun clearMocks() {
        clearMocks(welcomeService)
    }

    @Test
    fun `query welcome entry with missing auth returns unauthorized`() = withControllerTestApplication {
        coJustRun { welcomeService.getEntry("usr") }

        val call = handleRequest(HttpMethod.Get, "/welcome")
        with(call) {
            assertEquals(response.status(), HttpStatusCode.Unauthorized)
        }
    }

    @Test
    fun `get welcome entry for user`() = withControllerTestApplication {
        val expected = createWelcomeBody()
        coEvery { welcomeService.getEntry(expected.id) }.returns(expected)

        val call = handleRequest(HttpMethod.Get, "/welcome") {
            addHeader(HttpHeaders.Authorization, "Bearer "+createTokenWithRole("role"))
        }
        with(call) {
            val actual = response.content?.let {
                Json.decodeFromString<Welcome>(it)
            }
            assertEquals(response.status(), HttpStatusCode.OK)
            assertEquals(actual, expected)
        }
    }

    @Test
    fun `create welcome entry without admin role`() = withControllerTestApplication {

        val body = Json.encodeToString(createWelcomeBody())
        assertFailsWith<AuthorizationException> {
            handleRequest(HttpMethod.Post, "/welcome") {
                addHeader(HttpHeaders.Authorization, "Bearer "+createTokenWithRole("role"))
                addHeader(HttpHeaders.ContentType, ContentType.Application.Json.toString())
                setBody(body)
            }
        }
    }

    @Test
    fun `create welcome entry`() = withControllerTestApplication {

        val request = createWelcomeBody()
        val body = Json.encodeToString(request)

        coEvery { welcomeService.addEntry(request) }.returns(request)

        val call = handleRequest(HttpMethod.Post, "/welcome") {
            addHeader(HttpHeaders.Authorization, "Bearer "+createTokenWithRole("admin"))
            addHeader(HttpHeaders.ContentType, ContentType.Application.Json.toString())
            setBody(body)
        }
        with(call) {
            val actual = response.content?.let {
                Json.decodeFromString<Welcome>(it)
            }
            assertEquals(response.status(), HttpStatusCode.Created)
            assertEquals(actual, request)
        }
    }

    @Test
    fun `update cv entry`() = withControllerTestApplication {

        val request = createWelcomeBody()
        val body = Json.encodeToString(request)

        coEvery { welcomeService.updateEntry(request.id, request) }.returns(request)

        val call = handleRequest(HttpMethod.Put, "/welcome/"+request.id) {
            addHeader(HttpHeaders.Authorization, "Bearer "+createTokenWithRole("admin"))
            addHeader(HttpHeaders.ContentType, ContentType.Application.Json.toString())
            setBody(body)
        }
        with(call) {
            val actual = response.content?.let {
                Json.decodeFromString<Welcome>(it)
            }
            assertEquals(response.status(), HttpStatusCode.OK)
            assertEquals(actual, request)
        }
    }

    @Test
    fun `delete cv entry`() = withControllerTestApplication {

        val id = "someusr"
        coJustRun { welcomeService.deleteEntry(id) }

        val call = handleRequest(HttpMethod.Delete, "/welcome/$id") {
            addHeader(HttpHeaders.Authorization, "Bearer "+createTokenWithRole("admin"))
        }
        with(call) {
            assertEquals(response.status(), HttpStatusCode.OK)
        }
    }

    private fun createWelcomeBody(): Welcome {
        return Welcome("someuser", "someTitle", "message")
    }
}