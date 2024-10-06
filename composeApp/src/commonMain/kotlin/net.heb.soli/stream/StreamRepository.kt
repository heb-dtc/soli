package net.heb.soli.stream

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import kotlinx.datetime.LocalDate
import net.heb.soli.db.AmbientEntity
import net.heb.soli.db.AppDatabase
import net.heb.soli.db.PlayedStatusUpdate
import net.heb.soli.db.PodcastEpisodeEntity
import net.heb.soli.db.PodcastFeedEntity
import net.heb.soli.db.RadioEntity
import net.heb.soli.db.RemotePodcastEpisode
import net.heb.soli.db.TimeCodeUpdate
import net.heb.soli.db.TrackEntity
import net.heb.soli.network.SoliApi

class StreamRepository(private val api: SoliApi, private val db: AppDatabase) {

    suspend fun sync() {
        getStreams().forEach {
            when (it) {
                is StreamItem.RadioItem -> {
                    db.radioDao().upsert(
                        RadioEntity(
                            id = it.id,
                            name = it.name,
                            url = it.uri
                        )
                    )
                }

                is StreamItem.AmbientItem -> {
                    db.ambientDao().upsert(
                        AmbientEntity(
                            id = it.id,
                            name = it.name,
                            url = it.uri
                        )
                    )
                }

                is StreamItem.SongItem -> {
                    db.trackEntityDao().upsert(
                        TrackEntity(
                            id = it.id,
                            name = it.name,
                            url = it.uri,
                            duration = it.duration
                        )
                    )
                }

                is StreamItem.PodcastFeedItem -> {
                    db.podcastFeedDao().upsert(
                        PodcastFeedEntity(
                            id = it.id,
                            name = it.name,
                            imageUrl = it.coverUrl,
                        )
                    )
                }

                is StreamItem.SpotifyPlaylistItem -> TODO()
                is StreamItem.PodcastEpisodeItem -> {
                    db.podcastEpisodeEntityDao().insert(
                        PodcastEpisodeEntity(
                            id = it.id,
                            name = it.name,
                            url = it.uri,
                            duration = it.duration,
                            timeCode = it.timeCode,
                            played = it.played,
                            feedId = it.feedId,
                            description = it.description,
                            datePublished = it.date.toEpochDays(),
                        )
                    )
                }
            }
        }
    }

    private suspend fun getStreams(): List<StreamItem> {
        return withContext(Dispatchers.IO) {
            val radios = api.getLibrary().radios.map {
                StreamItem.RadioItem(
                    id = it.id,
                    name = it.name,
                    uri = it.url,
                )
            }

            val ambient = api.getLibrary().ambient.map {
                StreamItem.AmbientItem(
                    id = it.id,
                    name = it.name,
                    uri = it.url,
                )
            }

            val songs = api.getLibrary().songs.map {
                StreamItem.SongItem(
                    id = it.id,
                    name = it.name,
                    uri = it.url,
                    duration = it.duration
                )
            }

            val podcasts = api.getLibrary().podcasts.map {
                StreamItem.PodcastFeedItem(
                    id = it.id,
                    name = it.name,
                    uri = it.url,
                    coverUrl = it.coverUrl
                )
            }

//            val episodes = api.getLibrary().podcastEpisodes.map {
//                StreamItem.PodcastEpisodeItem(
//                    id = it.id,
//                    name = it.name,
//                    uri = it.url,
//                    remoteId = it.remoteId,
//                    duration = it.duration,
//                    timeCode = it.timeCode,
//                    played = it.played,
//                    feedId = it.feedId
//                )
//            }

            return@withContext radios + ambient + songs + podcasts
        }
    }

    fun observeRadios(): Flow<List<StreamItem>> {
        return db.radioDao().observe()
            .map { entity ->
                entity.map {
                    StreamItem.RadioItem(
                        id = it.id,
                        name = it.name,
                        uri = it.url,
                    )
                }
            }.flowOn(Dispatchers.IO)
    }

    fun observeAmbientStream(): Flow<List<StreamItem>> {
        return db.ambientDao().observe()
            .map { entity ->
                entity.map {
                    StreamItem.AmbientItem(
                        id = it.id,
                        name = it.name,
                        uri = it.url,
                    )
                }
            }.flowOn(Dispatchers.IO)
    }

    fun observerPodcastFeeds(): Flow<List<StreamItem>> {
        return db.podcastFeedDao().observe()
            .map { entity ->
                entity.map {
                    StreamItem.PodcastFeedItem(
                        id = it.id,
                        name = it.name,
                        uri = it.imageUrl,
                        coverUrl = it.imageUrl
                    )
                }
            }.flowOn(Dispatchers.IO)
    }

    fun observeTracks(): Flow<List<StreamItem>> {
        return db.trackEntityDao().observe()
            .map { entity ->
                entity.map {
                    StreamItem.SongItem(
                        id = it.id,
                        name = it.name,
                        uri = it.url,
                        duration = it.duration
                    )
                }
            }.flowOn(Dispatchers.IO)
    }

    fun observePodcastEpisodes(podcastId: Long): Flow<List<StreamItem.PodcastEpisodeItem>> {
        return db.podcastEpisodeEntityDao().observe(podcastId)
            .map { entity ->
                entity.map {
                    StreamItem.PodcastEpisodeItem(
                        id = it.id,
                        name = it.name,
                        uri = it.url,
                        duration = it.duration,
                        timeCode = it.timeCode,
                        played = it.played,
                        feedId = it.feedId,
                        description = it.description,
                        date = LocalDate.fromEpochDays(it.datePublished)
                    )
                }
            }.flowOn(Dispatchers.IO)
    }

    suspend fun fetchPodcastEpisodes(feedId: Long) {
        withContext(Dispatchers.IO) {
            api.getPodcastEpisodes(feedId).map { episode ->
                db.podcastEpisodeEntityDao().upsert(
                    RemotePodcastEpisode(
                        id = episode.id,
                        name = episode.name,
                        url = episode.uri,
                        duration = episode.duration,
                        feedId = episode.feedId,
                        description = episode.description,
                        datePublished = episode.date.toEpochDays()
                    )
                )
            }
        }
    }

    fun observeSpotifyStreams(): Flow<List<StreamItem>> {
        return flow {
            emit(getSpotifyStreams())
        }.flowOn(Dispatchers.IO)
    }

    private fun getSpotifyStreams() = emptyList<StreamItem>() // api.getSpotifyLikedTracks()

    suspend fun getPodcastFeed(podcastId: Long): StreamItem.PodcastFeedItem? {
        return withContext(Dispatchers.IO) {
            return@withContext db.podcastFeedDao().get(podcastId)?.let {
                StreamItem.PodcastFeedItem(
                    id = it.id,
                    uri = it.imageUrl,
                    name = it.name,
                    coverUrl = it.imageUrl
                )
            }
        }
    }

    suspend fun updateEpisodeTimeCode(id: Long, progress: Long) {
        withContext(Dispatchers.IO) {
            db.podcastEpisodeEntityDao()
                .updateTimeCode(TimeCodeUpdate(id = id, timeCode = progress))
        }
    }

    suspend fun updateEpisodePlayedStatus(id: Long, played: Boolean) {
        withContext(Dispatchers.IO) {
            db.podcastEpisodeEntityDao().updatePlayedStatus(
                PlayedStatusUpdate(
                    id = id,
                    played = played
                )
            )
        }
    }
}