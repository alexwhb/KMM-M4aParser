import Atoms.Mdhd
import Atoms.SampleTableAtoms.Stbl
import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.http.*
import kotlinx.coroutines.MainScope
import platform.Foundation.NSDocumentDirectory
import platform.Foundation.NSFileManager
import platform.Foundation.NSURL
import platform.Foundation.NSUserDomainMask
import utils.WebByteStreamReader

private val httpClient = HttpClient {
    followRedirects = false
}

// TODO we'll get rid of this
val scope = MainScope()

//suspend fun getByUrl(url: String, storeCode: String, sessionId: String): Stbl {
//    val headers = listOf(Pair("X-API-KEY", sessionId), Pair("X-STORE-CODE", storeCode))
//    val reader = WebByteStreamReader(httpClient, url, headers)
//    val parser = M4aParser(reader)
//    parser.parse()
//    return parser.moov.tracks.first().mdia.minf.stbl
//}

data class Res(val stbl: Stbl, val mdhd: Mdhd, val url: String?)

suspend fun getByUrl(): Res {
    val url = getTrackURL()!!

    val reader = WebByteStreamReader(httpClient, url)
    val parser = M4aParser(reader)
    parser.parse()
    val mdia = parser.moov.tracks.first().mdia
    val mdhd = mdia.mdhd
    val stbl = mdia.minf.stbl

    return Res(stbl, mdhd, url)
}

suspend fun getTrackURL(): String? {
    val response = httpClient.get("https://app.downpour.com/v2/book/bd9827/track/9827-003.m4a") {
        headers {
            append("X-API-STORE-CODE", "DOWNPOUR")
            append("X-API-KEY", "f6b3ad11-3483-4b13-b683-907d89e26bce")
        }
    }

    if (response.status == HttpStatusCode.Found) {  // 302
        return response.headers[HttpHeaders.Location] as String
    }
    return null
}


suspend fun getByFilePath(fileName: String): Stbl {
    val documentDirectory: NSURL = NSFileManager.defaultManager.URLsForDirectory(
        NSDocumentDirectory,
        NSUserDomainMask,
    ).last() as NSURL

    val fullPath = "${documentDirectory.path}/$fileName"
    val reader = FileByteStreamReader(fullPath)
    val parser = M4aParser(reader)
    parser.parse()
    return parser.moov.tracks.first().mdia.minf.stbl
}