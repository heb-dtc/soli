@file:OptIn(ExperimentalForeignApi::class)

package net.heb.soli.player

import kotlinx.cinterop.ExperimentalForeignApi
import net.heb.soli.stream.StreamItem
import platform.AVFAudio.AVAudioSession
import platform.AVFAudio.AVAudioSessionCategoryPlayback
import platform.AVFAudio.setActive
import platform.AVFoundation.AVAsset
import platform.AVFoundation.AVPlayer
import platform.AVFoundation.AVPlayerItem
import platform.AVFoundation.AVPlayerStatus
import platform.AVFoundation.AVPlayerTimeControlStatusPlaying
import platform.AVFoundation.AVURLAsset
import platform.AVFoundation.currentItem
import platform.AVFoundation.duration
import platform.AVFoundation.metadata
import platform.AVFoundation.pause
import platform.AVFoundation.play
import platform.AVFoundation.replaceCurrentItemWithPlayerItem
import platform.AVFoundation.timeControlStatus
import platform.AVFoundation.tracks
import platform.Foundation.NSURL

actual class PlayerBuilder {
    actual fun build(): Player {
        return Player(PlatformPlayer())
    }
}

actual class PlatformPlayer {

    private val player: AVPlayer = AVPlayer()

    init {
        try {
            AVAudioSession.sharedInstance().setCategory(
                category = AVAudioSessionCategoryPlayback,
                error = null
            )
            AVAudioSession.sharedInstance().setActive(
                true,
                null
            )
        } catch (e: Exception) {
            println("Failed to setup audio session: ${e.message}")
        }
    }
    actual fun play(item: StreamItem) {
        println("play")
        val url = NSURL.URLWithString(item.uri)!!
        val playerItem = AVPlayerItem(url)
        player.replaceCurrentItemWithPlayerItem(playerItem)
        player.play()
    }

    actual fun pause() {
        println("pause")
        player.pause()
    }

    actual fun stop() {
        println("stop")
    }

    actual fun resume() {
        println("resume")
    }

    actual fun isPlaying(): Boolean {
        return player.timeControlStatus == AVPlayerTimeControlStatusPlaying
    }
}