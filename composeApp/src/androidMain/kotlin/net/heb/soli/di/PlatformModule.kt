package net.heb.soli.di

import net.heb.soli.player.Player
import net.heb.soli.player.PlayerBuilder
import org.koin.dsl.module

actual fun platformModule() = module {
    single<Player> {
        PlayerBuilder(get()).build()
    }
}