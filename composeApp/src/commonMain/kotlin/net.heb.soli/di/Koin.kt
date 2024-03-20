package net.heb.soli.di

import net.heb.soli.stream.StreamRepository
import org.koin.core.KoinApplication
import org.koin.core.context.startKoin
import org.koin.core.module.Module
import org.koin.dsl.module

fun initKoin(appDeclaration: (KoinApplication.() -> Unit)? = null) = startKoin {
    if (appDeclaration != null) {
        appDeclaration()
    }
    modules(commonModule(), platformModule())
}

expect fun platformModule(): Module

fun commonModule() = module {
    single<StreamRepository> {
        StreamRepository()
    }
}