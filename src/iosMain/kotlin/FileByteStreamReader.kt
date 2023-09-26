import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.addressOf
import kotlinx.cinterop.usePinned
import platform.Foundation.*
import platform.posix.memcpy
import utils.ByteStreamReader
import utils.SimpleByteArrayReader

@OptIn(ExperimentalForeignApi::class)
class FileByteStreamReader(path: String) : ByteStreamReader() {
    private val fileHandle = NSFileHandle.fileHandleForReadingAtPath(path)

    override var totalSize: Long = NSFileManager.defaultManager.attributesOfItemAtPath(path, null)?.get("NSFileSize") as Long? ?: 0L
    override val sizeAvailable: Long
        get() = buffer.size - cursor

    private val CHUNK_SIZE = 4096

    private var buffer = ByteArray(0)

    private fun fetchNextChunk() {
        checkNotNull(fileHandle) {"Our file handle cannot be null"}

        fileHandle.seekToFileOffset(buffer.size.toULong())
        val chunk = fileHandle.readDataOfLength(CHUNK_SIZE.toULong()).toByteArray()
        if (chunk.isNotEmpty()) {
            println("first four bytes: ${chunk.take(4)}")
            println("last four bytes: ${chunk.takeLast(4)}")
            buffer += chunk
            println("fetchedBytes size: ${chunk.size} buffer total size: ${buffer.size}")
        }
    }

    private fun ensureBufferIsSufficient(n: Int) {
        while (sizeAvailable < n) {
            fetchNextChunk()
        }
    }

    override suspend fun extractFirst(n: Int): ByteArray {
        check(n > 0)
        ensureBufferIsSufficient(n)

        val result = buffer.copyOfRange(cursor.toInt(), (cursor + n).toInt())
        check(result.size == n) {"result should be the desired length"}
        cursor += n
        return result
    }

    override suspend fun skip(n: Int) {
        ensureBufferIsSufficient(n)
        cursor += n
    }

    override fun createNewInstance(data: ByteArray): ByteStreamReader {
        return SimpleByteArrayReader(data.copyOf(), data.size.toLong(), data.size.toLong())
    }
}

@OptIn(ExperimentalForeignApi::class)
fun NSData.toByteArray(): ByteArray {
    return ByteArray(length.toInt()).apply {
        usePinned {
            memcpy(it.addressOf(0), bytes, length)
        }
    }
}