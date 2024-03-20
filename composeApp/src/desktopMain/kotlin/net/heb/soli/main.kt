package net.heb.soli

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.runtime.Composable
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

@Preview
@Composable
fun AppDesktopPreview() {
    App()
}