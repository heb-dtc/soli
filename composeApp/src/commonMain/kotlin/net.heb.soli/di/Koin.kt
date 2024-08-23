package net.heb.soli.di

import io.ktor.client.HttpClient
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import net.heb.soli.HomeScreenViewModel
import net.heb.soli.stream.StreamRepository
import net.heb.soli.network.SoliApi
import org.koin.compose.viewmodel.dsl.viewModel
import org.koin.core.KoinApplication
import org.koin.core.context.startKoin
import org.koin.core.module.Module
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

fun initKoin(appDeclaration: (KoinApplication.() -> Unit)? = null) = startKoin {
    if (appDeclaration != null) {
        appDeclaration()
    }
    modules(commonModule(), platformModule())
}

expect fun platformModule(): Module

fun commonModule() = module {
    single {
        HttpClient {
            install(Logging) {
                logger = object : Logger {
                    override fun log(message: String) {
                        print(message = message)
                    }
                }
                level = LogLevel.ALL
            }
            install(ContentNegotiation) {
                json(Json {
                    prettyPrint = true
                    isLenient = true
                    ignoreUnknownKeys = true
                })
            }
        }
    }

    singleOf(::SoliApi)
    singleOf(::StreamRepository)

    viewModel { HomeScreenViewModel(get()) }
}