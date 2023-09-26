import utils.ByteStreamReader
import utils.SimpleByteArrayReader
import java.io.File
import java.io.RandomAccessFile


class FileByteStreamReader(private val file: RandomAccessFile) : ByteStreamReader() {
    override var totalSize: Long = file.length()
        private set
    override val sizeAvailable: Long
        get() = buffer.size - cursor

    private val CHUNK_SIZE = 4096

    private var buffer = ByteArray(0)

    private fun fetchNextChunk() {
        file.seek(buffer.size.toLong())
        val chunk = ByteArray(CHUNK_SIZE)
        val bytesRead = file.read(chunk)
        if (bytesRead > 0) {
            println("first four bytes: ${chunk.take(4)}")
            println("last four bytes: ${chunk.takeLast(4)}")
            buffer += chunk.sliceArray(0 until bytesRead)
//            println("buffer last four bytes: ${buffer.takeLast(4)}")
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