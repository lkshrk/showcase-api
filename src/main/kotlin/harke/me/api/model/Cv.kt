package harke.me.api.model

import kotlinx.serialization.Serializable


@Serializable
data class Cv(
    val id: Int? = null,
    val title: String,
    val content: String,
    val startYear: Int,
    val endYear: Int
)