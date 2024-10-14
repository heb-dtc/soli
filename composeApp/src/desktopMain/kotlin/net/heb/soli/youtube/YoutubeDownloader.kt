package net.heb.soli.youtube

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import net.heb.soli.Platform
import java.io.File


actual class YoutubeDownloader(private val platform: Platform) {

    actual suspend fun download(url: String, name: String) {
        withContext(Dispatchers.IO) {
            val command = listOf("yt-dlp", "-x", "--audio-format", "mp3", "-o", name, url)
            val processBuilder = ProcessBuilder(command).also { builder ->
                "${platform.getAppDirectoryPath()}/youtube".let(::File).let(builder::directory)
            }

            val process = try {
                processBuilder.start()
            } catch (e: Exception) {
                throw Exception("Failed to start process", e)
            }

            println("Downloading $url")

            val exitCode = try {
                process.waitFor()
            } catch (e: Exception) {
                throw Exception("Failed to wait for process", e)
            }

            println("Download finished with exit code $exitCode")
        }
    }
}