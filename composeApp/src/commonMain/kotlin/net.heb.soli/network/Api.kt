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
import net.heb.soli.stream.EpisodeWrapper
import net.heb.soli.stream.FeedWrapper
import net.heb.soli.stream.Stream
import net.heb.soli.stream.StreamItem
import net.heb.soli.stream.StreamLibrary
import net.heb.soli.stream.StreamType

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

    suspend fun getPodcastFeed(feedId: String): StreamItem {
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

        return StreamItem(
            id = feedId.toLong(),
            name = wrapper.feed.title,
            uri = wrapper.feed.image,
            type = StreamType.PodcastFeed
        )
    }

    suspend fun getPodcastEpisodes(feedId: String): List<StreamItem> {
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
            StreamItem(
                id = it.id,
                name = it.title,
                uri = it.enclosureUrl,
                type = StreamType.PodcastEpisode
            )
        }.toList()
    }

    private fun HttpRequestBuilder.createHeaders() {
        header("Authorization", "Bearer 12ILK56UB")
    }
}