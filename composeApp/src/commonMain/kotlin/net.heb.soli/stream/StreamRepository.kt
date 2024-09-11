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

    private suspend fun getStreams(): List<StreamItem> {
        return withContext(Dispatchers.IO) {
            val items = api.getLibrary().streams.map {
                StreamItem(it.id, it.name, it.url, it.type)
            }

            return@withContext items
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

    fun observePodcastEpisodes(feedId: String): Flow<List<StreamItem>> {
        return flow {
            emit(getPodcastEpisodes(feedId))
        }.flowOn(Dispatchers.IO)
    }

    private suspend fun getPodcastEpisodes(feedId: String): List<StreamItem> {
        return api.getPodcastEpisodes(feedId)
    }

    fun observeSpotifyStreams(): Flow<List<StreamItem>> {
        return flow {
            emit(getSpotifyStreams())
        }.flowOn(Dispatchers.IO)
    }

    private suspend fun getSpotifyStreams() = emptyList<StreamItem>() // api.getSpotifyLikedTracks()
}