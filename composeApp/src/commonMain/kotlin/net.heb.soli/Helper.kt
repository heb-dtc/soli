package net.heb.soli

import org.koin.core.KoinApplication

fun initKoin(
    appDeclaration: (KoinApplication.() -> Unit)? = null
): KoinApplication = net.heb.soli.di.initKoin(appDeclaration)