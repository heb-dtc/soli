package net.heb.soli

import okio.FileSystem

actual class Platform {
    actual fun getAppDirectoryPath(): String {
        return "/home/flow/.local/share/soli/"
    }
}

actual val FILESYSTEM: FileSystem
    get() = FileSystem.SYSTEM