package net.heb.soli.network

import java.security.MessageDigest

actual class Crypto {
    actual fun sha1(data: String): String {
        return MessageDigest.getInstance("SHA-1").digest(data.toByteArray())
            .joinToString("") { "%02x".format(it) }
    }
}