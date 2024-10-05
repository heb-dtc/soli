package net.heb.soli.stream

import kotlinx.serialization.Serializable

@Serializable
data class StreamLibrary(
    val radios: List<Radio>,
    val ambient: List<Ambient>,
    val songs: List<Song>,
    val podcasts: List<PodcastFeed>,
    val spotifyPlaylists: List<SpotifyPlaylist>,
)

@Serializable
data class Radio(
    val id: Long,
    val name: String,
    val url: String,
)

@Serializable
data class Ambient(
    val id: Long,
    val name: String,
    val url: String,
)

@Serializable
data class Song(
    val id: Long,
    val name: String,
    val url: String,
    val duration: Long
)

@Serializable
data class SpotifyPlaylist(
    val id: Long,
    val name: String,
    val url: String,
)

@Serializable
data class PodcastFeed(
    val id: Long,
    val remoteId: Long,
    val name: String,
    val url: String,
    val coverUrl: String
)

@Serializable
data class PodcastEpisode(
    val id: Long,
    val remoteId: Long,
    val feedId: Long,
    val name: String,
    val url: String,
    val duration: Long,
    val played: Boolean,
    val timeCode: Long
)

sealed class StreamItem(open val id: Long, open val uri: String, open val name: String) {
    data class RadioItem(
        override val id: Long,
        override val uri: String,
        override val name: String,
    ) : StreamItem(id, uri, name)

    data class AmbientItem(
        override val id: Long,
        override val uri: String,
        override val name: String,
    ) : StreamItem(id, uri, name)

    data class SongItem(
        override val id: Long,
        override val uri: String,
        override val name: String,
        val duration: Long,
    ) : StreamItem(id, uri, name)

    data class PodcastFeedItem(
        override val id: Long,
        override val uri: String,
        override val name: String,
        val remoteId: Long,
        val coverUrl: String,
    ) : StreamItem(id, uri, name)

    data class SpotifyPlaylistItem(
        override val id: Long,
        override val uri: String,
        override val name: String,
    ) : StreamItem(id, uri, name)

    data class PodcastEpisodeItem(
        override val id: Long,
        override val uri: String,
        override val name: String,
        val remoteId: Long,
        val feedId: Long,
        val duration: Long,
        val played: Boolean,
        val timeCode: Long,
    ) : StreamItem(id, uri, name)
}