package net.heb.soli.stream

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import net.heb.soli.db.AmbientEntity
import net.heb.soli.db.AppDatabase
import net.heb.soli.db.PodcastEpisodeEntity
import net.heb.soli.db.PodcastFeedEntity
import net.heb.soli.db.RadioEntity
import net.heb.soli.db.TrackEntity
import net.heb.soli.network.SoliApi

class StreamRepository(private val api: SoliApi, private val db: AppDatabase) {

    suspend fun insertAllStream() {
        db.radioDao().upsert(
            RadioEntity(
                name = "SomaFM",
                url = "https://ice2.somafm.com/groovesalad-256-mp3"
            )
        )
        db.radioDao().upsert(
            RadioEntity(
                name = "Radio Nova",
                url = "http://novazz.ice.infomaniak.ch/novazz-128.mp3"
            )
        )
        db.radioDao().upsert(
            RadioEntity(
                name = "France Info",
                url = "https://stream.radiofrance.fr/franceinfo/franceinfo.m3u8"
            )
        )
        db.radioDao().upsert(
            RadioEntity(
                name = "France Inter",
                url = "https://stream.radiofrance.fr/franceinter/franceinter.m3u8"
            )
        )
        db.radioDao().upsert(
            RadioEntity(
                name = "France Musique",
                url = "https://stream.radiofrance.fr/francemusique/francemusique.m3u8"
            )
        )
        db.radioDao().upsert(
            RadioEntity(
                name = "FIP",
                url = "https://stream.radiofrance.fr/fip/fip.m3u8"
            )
        )
        db.podcastFeedDao().upsert(
            PodcastFeedEntity(
                name = "Le rendez-vous Tech",
                imageUrl = "https://static.feedpress.com/logo/rdvtech-612f2a32d5aee.png",
                remoteId = 555224
            )
        )
        db.podcastFeedDao().upsert(
            PodcastFeedEntity(
                name = "Le Cosy Corner",
                imageUrl = "https://i1.sndcdn.com/avatars-x0N90uVMwjR6Lzg0-X9yEKw-original.jpg",
                remoteId = 509931
            )
        )
        db.podcastFeedDao().upsert(
            PodcastFeedEntity(
                name = "After Hate",
                imageUrl = "http://www.afterhate.fr/wp-content/uploads/2016/03/vignetteitunes-med.png",
                remoteId = 217966
            )
        )
        db.ambientDao().upsert(
            AmbientEntity(
                name = "Rain",
                url = "https://rainyday-mynoise.radioca.st/stream"
            )
        )
        db.ambientDao().upsert(
            AmbientEntity(
                name = "Space",
                url = "https://spaceodyssey-mynoise.radioca.st/stream"
            )
        )
    }

    suspend fun forceUpdateLibrary() {
        val radios = db.radioDao().get().map {
            Stream(it.id, it.name, it.url, StreamType.Radio)
        }
        val ambients = db.ambientDao().get().map {
            Stream(it.id, it.name, it.url, StreamType.Ambient)
        }
        val podcast = db.podcastFeedDao().get().map {
            Stream(it.id, it.name, it.imageUrl, StreamType.PodcastFeed)
        }

        val library = StreamLibrary(radios + ambients + podcast)
        api.updateLibrary(library)
    }

    suspend fun sync() {
        getStreams().forEach {
            when (it.type) {
                StreamType.Radio -> {
                    db.radioDao().upsert(
                        RadioEntity(
                            id = it.id,
                            name = it.name,
                            url = it.uri
                        )
                    )
                }

                StreamType.Ambient -> {
                    db.ambientDao().upsert(
                        AmbientEntity(
                            id = it.id,
                            name = it.name,
                            url = it.uri
                        )
                    )
                }

                StreamType.Song -> {
                    db.trackEntityDao().upsert(
                        TrackEntity(
                            id = it.id,
                            name = it.name,
                            url = it.uri
                        )
                    )
                }

                StreamType.PodcastFeed -> {
                    db.podcastFeedDao().upsert(
                        PodcastFeedEntity(
                            id = it.id,
                            name = it.name,
                            remoteId = it.id,
                            imageUrl = it.uri
                        )
                    )
                }

                StreamType.SpotifyPlaylist -> TODO()
                StreamType.PodcastEpisode -> {
                    db.podcastEpisodeEntityDao().upsert(
                        PodcastEpisodeEntity(
                            id = it.id,
                            name = it.name,
                            url = it.uri,
                            remoteId = it.id,
                        )
                    )
                }
            }
        }
    }

    fun observeRadios(): Flow<List<StreamItem>> {
        return db.radioDao().observe()
            .map { entity ->
                entity.map { StreamItem(it.id, it.name, it.url, StreamType.Radio) }
            }.flowOn(Dispatchers.IO)
    }

    fun observeAmbientStream(): Flow<List<StreamItem>> {
        return db.ambientDao().observe()
            .map { entity ->
                entity.map { StreamItem(it.id, it.name, it.url, StreamType.Ambient) }
            }.flowOn(Dispatchers.IO)
    }

    fun observerPodcastFeeds(): Flow<List<StreamItem>> {
        return db.podcastFeedDao().observe()
            .map { entity ->
                entity.map { StreamItem(it.id, it.name, it.imageUrl, StreamType.PodcastFeed) }
            }.flowOn(Dispatchers.IO)
    }

    fun observeTracks(): Flow<List<StreamItem>> {
        return db.trackEntityDao().observe()
            .map { entity ->
                entity.map { StreamItem(it.id, it.name, it.url, StreamType.Song) }
            }.flowOn(Dispatchers.IO)
    }

    fun observerPodcastEpisodes(): Flow<List<StreamItem>> {
        return db.podcastEpisodeEntityDao().observe()
            .map { entity ->
                entity.map { StreamItem(it.id, it.name, it.url, StreamType.PodcastEpisode) }
            }.flowOn(Dispatchers.IO)
    }

    private suspend fun getStreams(): List<StreamItem> {
        return withContext(Dispatchers.IO) {
            val items = api.getLibrary().streams.map {
                StreamItem(it.id, it.name, it.url, it.type)
            }

            return@withContext items
        }
    }

    private suspend fun getPodcastStreams(): List<StreamItem> {
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

    private suspend fun getPodcastEpisodes(feedId: String): List<StreamItem> {
        return api.getPodcastEpisodes(feedId)
    }

    private fun getSpotifyStreams() = emptyList<StreamItem>()

    private fun getAmbientStreams(): List<StreamItem> {
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