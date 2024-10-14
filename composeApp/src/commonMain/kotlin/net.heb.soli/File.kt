package net.heb.soli

import okio.FileSystem

expect class Platform {
    fun getAppDirectoryPath(): String
}

expect val FILESYSTEM: FileSystem