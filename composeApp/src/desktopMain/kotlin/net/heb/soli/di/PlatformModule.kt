package net.heb.soli.di

import androidx.room.RoomDatabase
import net.heb.soli.Platform
import net.heb.soli.db.AppDatabase
import net.heb.soli.getDatabaseBuilder
import net.heb.soli.player.Player
import net.heb.soli.player.PlayerBuilder
import net.heb.soli.youtube.YoutubeDownloader
import org.koin.dsl.module

actual fun platformModule() = module {
    single<Platform> {
        Platform()
    }

    single<Player> {
        PlayerBuilder(get()).build()
    }

    single<RoomDatabase.Builder<AppDatabase>> {
        getDatabaseBuilder()
    }

    single<YoutubeDownloader> {
        YoutubeDownloader(get())
    }
}