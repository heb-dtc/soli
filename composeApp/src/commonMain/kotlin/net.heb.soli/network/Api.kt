package net.heb.soli.network

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.get
import io.ktor.client.request.header
import net.heb.soli.stream.Stream

class SoliApi(
    private val httpClient: HttpClient
) {
    companion object {
        //TODO add possibility to switch between dev and prod from each individual app
        private const val BASE_URL = "https://music.hebus.net"
    }

    suspend fun getLibrary(): List<Stream> {
        val response = httpClient.get("$BASE_URL/library") {
            createHeaders()
        }

        print("response: $response")

        return response.body()
    }

    private fun HttpRequestBuilder.createHeaders() {
        header("Authorization", "Bearer 12ILK56UB")
    }
}