package net.heb.soli

import android.content.Context
import okio.FileSystem

actual class Platform(
    private val context: Context,
) {
    actual fun getAppDirectoryPath(): String {
        return context.filesDir.absolutePath
    }
}

actual val FILESYSTEM: FileSystem
    get() = FileSystem.SYSTEM