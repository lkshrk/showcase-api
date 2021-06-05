package harke.me.api.web.model

import kotlinx.serialization.Serializable


@Serializable
data class CvBody(
    val id: Int? = null,
    val title: String,
    val content: String,
    val startYear: Int,
    val endYear: Int
)