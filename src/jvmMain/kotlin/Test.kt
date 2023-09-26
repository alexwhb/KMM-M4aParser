import io.ktor.client.*
import io.ktor.client.engine.apache5.*
import kotlinx.coroutines.runBlocking
import utils.WebByteStreamReader
import java.io.RandomAccessFile


fun main() {

    val file  = RandomAccessFile("/User/someuser/file.m4a", "r")
    val fileReader = FileByteStreamReader(file)
    runBlocking {
        val parser = (M4aParser(fileReader))
        parser.parse()
        val mdhd = parser.moov.tracks.first().mdia.mdhd

        val duration = mdhd.duration/mdhd.timeScale

        println("Duration: $duration")
//        println()
        val stbl = parser.moov.tracks.first().mdia.minf.stbl
//        val test = parser.moov.tracks.first().mdia.minf.stbl.findPacketForTime(43.99401420354843, mdhd.timeScale)

////        val numPackets = parser.moov.tracks.first().mdia.minf.stbl.stsz.sampleSizeTable.count()
////        val test = parser.moov.tracks.first().mdia.minf.stbl.packetByteOffset(6863)
//        val first = parser.moov.tracks.first().mdia.minf.stbl.packetByteOffset(0)
//        val test = stbl.packetByteOffset(0)

        val test4 = stbl.packetByteOffset(1375)
        println(test4!! )



        // byte parsed offset: 40639

        val test2 = stbl.packetIndexFromByteOffset(613191)
        println(test2)

//        println(first)
//        println(test)
        val test3 = stbl.findPacketIndexFromConsecutiveSampleLengths(listOf(499, 725))
        println(test3)

//        println((test!! - first!!))
    }

//    val httpClient = HttpClient(Apache5) {
//        engine {
//            followRedirects = true
//            socketTimeout = 10_000
//            connectTimeout = 10_000
//            connectionRequestTimeout = 20_000
//        }
//
//    }
//    val url =
//        "http://someurl-to-m4a"
//    val reader = WebByteStreamReader(httpClient, url)
//    runBlocking {
//        val parser = M4aParser(reader)
//        parser.parse()
//        val test = parser.moov.tracks.first().mdia.minf.stbl.packetIndexForTime(30.0)
//        println(test)
//    }


}