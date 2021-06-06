package harke.me.api.model

import kotlinx.serialization.Serializable

@Serializable
data class Welcome(
    val id: String,
    val title: String,
    val coverLetter: String
)