package net.heb.soli.network

import java.security.MessageDigest


actual class Crypto {
    @OptIn(ExperimentalStdlibApi::class)
    actual fun sha1(data: String): String {
        val messageDigest = MessageDigest.getInstance("SHA-1")
        val digest = messageDigest.digest(data.toByteArray())
        return digest.toHexString()
    }
}