package net.heb.soli.spotify

import kotlinx.serialization.Serializable

@Serializable
data class UserPlaylists(
    val items: List<SimplifiedPlaylist>
)

@Serializable
data class SimplifiedPlaylist(
    val id: String,
    val name: String,
    val uri: String,
)
