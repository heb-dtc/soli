package net.heb.soli

import android.app.Application
import org.koin.android.ext.koin.androidContext

class SoliApp : Application() {
    override fun onCreate() {
        super.onCreate()
        initKoin {
            androidContext(this@SoliApp)
        }
    }
}