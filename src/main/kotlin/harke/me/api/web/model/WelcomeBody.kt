package harke.me.api.web.model

import kotlinx.serialization.Serializable

@Serializable
data class WelcomeBody(
    val id: String,
    val title: String,
    val coverLetter: String
)