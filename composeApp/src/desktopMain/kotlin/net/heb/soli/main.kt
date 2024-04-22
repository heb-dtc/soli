package net.heb.soli

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application

fun main() = application {
    initKoin()

    Window(
        title = "SOLI", onCloseRequest = ::exitApplication
    ) {
        App()
    }
}