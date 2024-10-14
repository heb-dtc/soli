package net.heb.soli.di

import androidx.room.RoomDatabase
import net.heb.soli.Platform
import net.heb.soli.db.AppDatabase
import net.heb.soli.db.getDatabaseBuilder
import net.heb.soli.player.Player
import net.heb.soli.player.PlayerBuilder
import net.heb.soli.youtube.YoutubeDownloader
import org.koin.dsl.module

actual fun platformModule() = module {
    single<Platform> {
        Platform(get())
    }
    single<Player> {
        PlayerBuilder(get(), get()).build()
    }

    single<RoomDatabase.Builder<AppDatabase>> {
        getDatabaseBuilder(get())
    }

    single<YoutubeDownloader> {
        YoutubeDownloader()
    }
}