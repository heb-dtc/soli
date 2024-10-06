package net.heb.soli.network

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDate
import net.heb.soli.spotify.UserPlaylists
import net.heb.soli.stream.EpisodeWrapper
import net.heb.soli.stream.FeedWrapper
import net.heb.soli.stream.StreamItem
import net.heb.soli.stream.StreamLibrary

class SoliApi(
    private val httpClient: HttpClient
) {
    companion object {
        //TODO add possibility to switch between dev and prod from each individual app
        private const val BASE_URL = "https://music.hebus.net"
    }

    suspend fun getLibrary(): StreamLibrary {
        val response = httpClient.get("$BASE_URL/library/soli/flow") {
            createHeaders()
        }

        print("response: $response")

        return response.body()
    }

    suspend fun updateLibrary(library: StreamLibrary) {
        val response = httpClient.post("$BASE_URL/library/soli/flow") {
            createHeaders()
            contentType(ContentType.Application.Json)
            setBody(library)
        }
        print("response: $response")
    }

    suspend fun getPodcastFeed(feedId: Long): StreamItem {
        val apiKey = "4SJ3XR6SF5N2SFEZ5ZZY"
        val apiSecret = "RxfyWNhPbx#sZRbTyJVz8GHfAwDDaCDjMdjWcSQ9"
        val now = Clock.System.now().toEpochMilliseconds() / 1000

        val sha1 = Crypto().sha1(apiKey + apiSecret + now)

        val response =
            httpClient.get("https://api.podcastindex.org/api/1.0/podcasts/byfeedid?id=$feedId&pretty") {
                header("User-Agent", "SoliApp/0.1")
                header("X-Auth-Key", apiKey)
                header("X-Auth-Date", now)
                header("Authorization", sha1)
            }

        val wrapper = response.body<FeedWrapper>()
        print("response: ${wrapper.feed.title}")

        return StreamItem.PodcastFeedItem(
            id = feedId,
            name = wrapper.feed.title,
            uri = wrapper.feed.image,
            coverUrl = wrapper.feed.image
        )
    }

    suspend fun getPodcastEpisodes(feedId: Long): List<StreamItem.PodcastEpisodeItem> {
        val apiKey = "4SJ3XR6SF5N2SFEZ5ZZY"
        val apiSecret = "RxfyWNhPbx#sZRbTyJVz8GHfAwDDaCDjMdjWcSQ9"
        val now = Clock.System.now().toEpochMilliseconds() / 1000

        val sha1 = Crypto().sha1(apiKey + apiSecret + now)

        val response =
            httpClient.get("https://api.podcastindex.org/api/1.0/episodes/byfeedid?id=$feedId&pretty") {
                header("User-Agent", "SoliApp/0.1")
                header("X-Auth-Key", apiKey)
                header("X-Auth-Date", now)
                header("Authorization", sha1)
            }

        val wrapper = response.body<EpisodeWrapper>()

        return wrapper.items.map {
            val durationInMs = it.duration * 1000
            StreamItem.PodcastEpisodeItem(
                id = it.id,
                name = it.title,
                uri = it.enclosureUrl,
                feedId = feedId,
                duration = durationInMs.toLong(),
                timeCode = 0,
                played = false,
                description = it.description,
                date = LocalDate.fromEpochDays(it.datePublished)
            )
        }.toList()
    }

    private fun HttpRequestBuilder.createHeaders() {
        header("Authorization", "Bearer 12ILK56UB")
    }

    suspend fun getSpotifyLikedTracks(): List<StreamItem> {
        val response = httpClient.get("https://api.spotify.com/v1/users/heb_dtc/playlists") {
            header(
                "Authorization",
                "Bearer BQBf0lNzT-BHG0GU6wIjT9hjvMISddfEDwF_qlTSdO2fwnik7n84DdgTlXP79maz3lA343RoNYNiKD7S8S6JaY9IWIM_mnYmjxudqCZNg3ip5i9qrGc"
            )
        }

        val wrapper = response.body<UserPlaylists>()
        return wrapper.items.mapIndexed { index, it ->
            StreamItem.SpotifyPlaylistItem(
                id = 9999 + index.toLong(),
                name = it.name,
                uri = it.id,
            )
        }.toList()
    }
}