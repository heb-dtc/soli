package net.heb.soli.di

import androidx.room.RoomDatabase
import net.heb.soli.db.AppDatabase
import net.heb.soli.getDatabaseBuilder
import net.heb.soli.player.Player
import net.heb.soli.player.PlayerBuilder
import org.koin.dsl.module

actual fun platformModule() = module {
    single<Player> {
        PlayerBuilder().build()
    }

    single<RoomDatabase.Builder<AppDatabase>> {
        getDatabaseBuilder()
    }
}