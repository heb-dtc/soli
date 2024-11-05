package net.heb.soli.youtube

import net.heb.soli.FILESYSTEM
import net.heb.soli.Platform
import net.heb.soli.stream.StreamRepository
import net.heb.soli.stream.getLocalYoutubeFileName
import okio.Path.Companion.toPath

expect class YoutubeDownloader {
    suspend fun download(url: String, name: String)
}

class YoutubeDownloadService(
    private val youtubeDownloader: YoutubeDownloader,
    private val streamRepository: StreamRepository,
    private val platform: Platform
) {
    suspend fun sync() {
        println("Syncing youtube streams")

        if (!FILESYSTEM.exists(platform.getAppDirectoryPath().toPath().resolve("youtube"))) {
            FILESYSTEM.createDirectory(platform.getAppDirectoryPath().toPath().resolve("youtube"))
        }

        val stream = streamRepository.getYoutubeStreamItems()
        stream.forEach {
            if (!it.downloaded) {
                // check if the file has been downloaded already
                val youtubeItemsPath = platform.getAppDirectoryPath().toPath().resolve("youtube")
                val fileName = getLocalYoutubeFileName(it.youtubeUrl)
                if (FILESYSTEM.exists(youtubeItemsPath.resolve(fileName))) {
                    // TODO if file exists, mark it as downloaded?
                    streamRepository.markAsDownloaded(it)
                    return@forEach
                } else {
                    // TODO handle error
                    youtubeDownloader.download(it.youtubeUrl, fileName)
                    streamRepository.markAsDownloaded(it)
                }
            }
        }
        println("All youtube stream have been synced")
    }
}