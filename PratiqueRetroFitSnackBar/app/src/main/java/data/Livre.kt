package data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Livre(
    @SerialName("title")
    val titre: String,
    @SerialName("author_name")
    val auteurs: List<String>? = null,
    @SerialName("first_publish_year")
    val anneePublication: Int? = null
)
@Serializable
data class ReponseLivre(
    @SerialName("docs")
    val livres: List<Livre>
)