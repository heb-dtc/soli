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

@Serializable
data class EpisodeWrapper(
    val items: List<Episode>
)

@Serializable
data class Episode(
    val id: Long,
    val title: String,
    val description: String,
    val enclosureUrl: String,
    val duration: Int,
    val datePublished: Int,
    val image: String,
)