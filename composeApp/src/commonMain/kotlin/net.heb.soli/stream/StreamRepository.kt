package net.heb.soli.stream

import net.heb.soli.network.SoliApi

class StreamRepository(private val api: SoliApi) {

    suspend fun getStreams(): List<StreamItem> {

        val items = api.getLibrary().map {
            if (it.name.contains("dikro", ignoreCase = true) || it.name.contains("compile2noel", ignoreCase = true)) {
                StreamItem(it.id, it.name, it.url, StreamType.Song)
            } else {
                StreamItem(it.id, it.name, it.url, StreamType.Radio)
            }
        }.toMutableList() + getAmbientStreams() + getPodcastStreams() + getSpotifyStreams()

        return items
    }

    fun getPodcastStreams() = emptyList<StreamItem>()

    fun getSpotifyStreams() = emptyList<StreamItem>()

    fun getAmbientStreams(): List<StreamItem> {
        return listOf(
            StreamItem(
                1234,
                "rain",
                "https://rainyday-mynoise.radioca.st/stream",
                StreamType.Ambient
            ),
            StreamItem(
                4311,
                "space",
                " https://spaceodyssey-mynoise.radioca.st/stream",
                StreamType.Ambient
            )
        )
    }
}