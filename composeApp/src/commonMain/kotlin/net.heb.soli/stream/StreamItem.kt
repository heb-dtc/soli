package net.heb.soli.stream

import kotlinx.serialization.Serializable

enum class StreamType {
    Radio, Ambient, Song, PodcastFeed, SpotifyPlaylist, PodcastEpisode
}

@Serializable
data class Stream(
    val id: Long,
    val name: String,
    val url: String
)

data class StreamItem(val id: Long, val name: String, val uri: String, val type: StreamType)