package utils

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.client.statement.*


class WebByteStreamReader(
    private val client: HttpClient,
    private val url: String,
    private val extraHeaders: List<Pair<String, String>> = emptyList(),
    private var buffer: ByteArray = ByteArray(0)
) : ByteStreamReader() {
    override var totalSize: Long = 10_000_000
        private set

    override val sizeAvailable: Long
        get() = buffer.size - cursor


    /**
     * The CHUNK_SIZE of 4096 bytes (or 4 KB) isn't strictly necessary; it's just a commonly used value that represents a
     * compromise between minimizing the number of network requests and not downloading too much data at once.
     * This value is often chosen because it matches the block size used by many file systems, and it is a
     * multiple of the typical size of a network packet (often 1500 bytes for Ethernet).
     * */
    private val CHUNK_SIZE = 16384  // Example chunk size

    /***
     * we start by fetching our URL's header for content length. This is just to populate the totalSize value
     */
    private suspend fun fetchNextChunk() {
        val resp: HttpResponse = client.get(url) {
            headers {
                append("Range", "bytes=${buffer.size}-${buffer.size + CHUNK_SIZE - 1}")
                extraHeaders.forEach { (key, value) ->
                    append(key, value)
                }
            }
        }

        if (resp.status.value in 200..299) {
            val fetchedBytes = resp.body<ByteArray>()
            check(fetchedBytes.size == CHUNK_SIZE)
            println("first four bytes: ${fetchedBytes.take(4)}")
            println("last four bytes: ${fetchedBytes.takeLast(4)}")

            buffer += fetchedBytes

            println("fetchedBytes size: ${fetchedBytes.size} buffer total size: ${buffer.size}")
        } else {
            throw Exception("HTTP Status code was not acceptable: ${resp.status.value}")
        }
    }

    private suspend fun ensureBufferIsSufficient(n: Int) {
        while (sizeAvailable < n) {
            fetchNextChunk()
        }
    }

    override suspend fun extractFirst(n: Int): ByteArray {
        check(n > 0)
        ensureBufferIsSufficient(n)

        val result = buffer.copyOfRange(cursor.toInt(), (cursor + n).toInt())
        check(result.size == n) { "result should be the desired length" }

        cursor += n // Updating the cursor here to point to the next unread byte in the file
        return result
    }

    override suspend fun skip(n: Int) {
        ensureBufferIsSufficient(n)
        cursor += n // Updating the cursor here to point to the next unread byte in the file
    }

    override fun createNewInstance(data: ByteArray): ByteStreamReader {
        return SimpleByteArrayReader(data.copyOf(), data.size.toLong(), data.size.toLong())
    }
}

