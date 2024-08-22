package net.heb.soli.stream

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.withContext
import net.heb.soli.network.SoliApi

class StreamRepository(private val api: SoliApi) {

    suspend fun getStreams(): List<StreamItem> {
        return withContext(Dispatchers.IO) {
            val items = api.getLibrary().map {
                if (it.name.contains("dikro", ignoreCase = true) || it.name.contains(
                        "compile2noel",
                        ignoreCase = true
                    )
                ) {
                    StreamItem(it.id, it.name, it.url, StreamType.Song)
                } else {
                    StreamItem(it.id, it.name, it.url, StreamType.Radio)
                }
            }.toMutableList() + getAmbientStreams() + getPodcastStreams() + getSpotifyStreams()

            return@withContext items
        }
    }

    suspend fun getPodcastStreams(): List<StreamItem> {
        return listOf(api.getPodcast("555224"), api.getPodcast("217966"),
            api.getPodcast("555224"), api.getPodcast("509931"))
    }

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