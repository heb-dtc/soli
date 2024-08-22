package net.heb.soli.stream

import kotlinx.serialization.Serializable

@Serializable
data class FeedWrapper(
    val feed: Feed
)

@Serializable
data class Feed(
    val id: Long,
    val title: String,
    val description: String,
    val author: String,
    val image: String,
)