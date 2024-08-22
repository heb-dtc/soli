package net.heb.soli.network

expect class Crypto() {
    fun sha1(data: String): String
}
