package net.heb.soli.stream

import net.heb.soli.network.SoliApi

class StreamRepository(private val api: SoliApi) {
    suspend fun getStreams(): List<StreamItem> {

        val items = api.getLibrary().map {
            StreamItem(it.id, it.name, it.url)
        }.toMutableList()

        items.add(StreamItem(1234, "rain", "https://rainyday-mynoise.radioca.st/stream"))
        items.add(StreamItem(4311, "space", " https://spaceodyssey-mynoise.radioca.st/stream"))

        return items
    }
}