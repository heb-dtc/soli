package net.heb.soli.stream

class StreamRepository {
    fun getStreams(): List<StreamItem> {
        return listOf(
            StreamItem(1, "soma", "https://ice2.somafm.com/groovesalad-256-mp3"),
            StreamItem(
                2,
                "france info",
                "https://stream.radiofrance.fr/franceinfo/franceinfo.m3u8"
            ),
            StreamItem(
                3,
                "france inter",
                "https://stream.radiofrance.fr/franceinter/franceinter.m3u8"
            ),
            StreamItem(
                4,
                "france musique",
                "https://stream.radiofrance.fr/francemusique/francemusique.m3u8"
            ),
            StreamItem(5, "fip", "https://stream.radiofrance.fr/fip/fip.m3u8"),
            StreamItem(6, "nova", "http://novazz.ice.infomaniak.ch/novazz-128.mp3")
        )
    }
}