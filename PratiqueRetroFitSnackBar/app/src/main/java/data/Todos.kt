package data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Todo(
    @SerialName("userId") val idUtilisateur: Int,
    @SerialName("id") val idTodo: Int,
    @SerialName("title") val titre: String,
    @SerialName("completed") val complete: Boolean
)