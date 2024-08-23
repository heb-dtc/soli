package net.heb.soli.stream

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext
import net.heb.soli.network.SoliApi

class StreamRepository(private val api: SoliApi) {

    fun observeStreams(): Flow<List<StreamItem>> {
        return flow {
            emit(getStreams())
        }.flowOn(Dispatchers.IO)
    }

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
        return listOf(
            api.getPodcastFeed("555224"), api.getPodcastFeed("217966"),
            api.getPodcastFeed("555224"), api.getPodcastFeed("509931")
        )
    }

    fun observePodcastEpisodes(feedId: String): Flow<List<StreamItem>> {
        return flow {
            emit(getPodcastEpisodes(feedId))
        }.flowOn(Dispatchers.IO)
    }

    suspend fun getPodcastEpisodes(feedId: String): List<StreamItem> {
        return api.getPodcastEpisodes(feedId)
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