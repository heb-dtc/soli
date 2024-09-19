package net.heb.soli

import org.koin.core.KoinApplication

fun initKoin(
    appDeclaration: (KoinApplication.() -> Unit)? = null
): KoinApplication = net.heb.soli.di.initKoin(appDeclaration)

fun Long.toDuration(): String {
    val totalSeconds = this / 1000
    val hours = totalSeconds / 3600
    val minutes = (totalSeconds % 3600) / 60
    val remainingSeconds = totalSeconds % 60
    if (hours == 0L) return "${minutes.toString().padStart(2, '0')}:${
        remainingSeconds.toString().padStart(2, '0')
    }"
    return "${hours.toString().padStart(2, '0')}:${
        minutes.toString().padStart(2, '0')
    }:${remainingSeconds.toString().padStart(2, '0')}"
}