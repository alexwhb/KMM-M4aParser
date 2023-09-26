package utils

import Atom
import Atoms.PassThrough
import enums.AtomIdentifier

//fun emptyByteArrayReader(): ByteArrayReader {
//    return ByteArrayReader(byteArrayOf())
//}

abstract class ByteStreamReader {
    protected var cursor: Long = 0
    protected abstract val totalSize: Long

    protected abstract val sizeAvailable: Long

    val size: Long
        get() = totalSize - cursor

    abstract suspend fun extractFirst(n: Int): ByteArray

    abstract suspend fun skip(n:Int)


    suspend fun extractToInt(n: Int): Int {
        require(n in 1..4) { "Size should be between 1 and 4: $n" }
        val extractedBytes = extractFirst(n)
        var result = 0
        for (i in 0..<n) {
            result = result shl 8 or (extractedBytes[i].toInt() and 0xFF)
        }
        return result
    }

    suspend fun extractToDouble(k: Int): Double {
        val extracted = extractFirst(k)

        return when (k) {
            1 -> extracted.toUByte().toDouble()
            2 -> extracted.extractUInt16BE().toDouble()
            4 -> extracted.extractUInt32BE().toDouble()
            8 -> extracted.extractUInt64BE().toDouble()
            else -> extracted.extractUInt32BE().toDouble()
        }
    }

    private fun ByteArray.extractUInt16BE(): Int {
        if (this.size < 2) throw IllegalArgumentException("Byte array is too short!")
        return (this[0].toInt() and 0xFF shl 8) or
                (this[1].toInt() and 0xFF)
    }

    private fun ByteArray.extractUInt32BE(): Long {
        if (this.size < 4) throw IllegalArgumentException("Byte array is too short!")
        return (this[0].toLong() and 0xFF shl 24) or
                (this[1].toLong() and 0xFF shl 16) or
                (this[2].toLong() and 0xFF shl 8) or
                (this[3].toLong() and 0xFF)
    }

    private fun ByteArray.extractUInt64BE(): Long {
        if (this.size < 8) throw IllegalArgumentException("Byte array is too short!")
        return (this[0].toLong() and 0xFF shl 56) or
                (this[1].toLong() and 0xFF shl 48) or
                (this[2].toLong() and 0xFF shl 40) or
                (this[3].toLong() and 0xFF shl 32) or
                (this[4].toLong() and 0xFF shl 24) or
                (this[5].toLong() and 0xFF shl 16) or
                (this[6].toLong() and 0xFF shl 8) or
                (this[7].toLong() and 0xFF)
    }
//    suspend fun extractToDouble(n: Int): Double {
//        val extractedBytes = extractFirst(n)
//        var longBits = 0L
//        for (i in 0..< n) {
//            longBits = longBits shl 8 or (extractedBytes[i].toLong() and 0xFF)
//        }
//        return Double.fromBits(longBits)
//    }

    suspend fun extractToUShort(): UShort {
        val extractedBytes = extractFirst(2)
        return ((extractedBytes[0].toInt() and 0xFF) shl 8 or (extractedBytes[1].toInt() and 0xFF)).toUShort()
    }

    suspend fun extractToUInt(): UInt {
        val extractedBytes = extractFirst(4)
        return ((extractedBytes[0].toInt() and 0xFF) shl 24 or
                (extractedBytes[1].toInt() and 0xFF) shl 16 or
                (extractedBytes[2].toInt() and 0xFF) shl 8 or
                (extractedBytes[3].toInt() and 0xFF)).toUInt()
    }

    suspend fun extractToUByte(): UByte {
        val extractedBytes = extractFirst(1)
        return extractedBytes[0].toUByte()
    }


    abstract fun createNewInstance(data: ByteArray): ByteStreamReader

    suspend fun extractAndParseToAtom(): Atom {
        val preliminarySize = extractToInt(4)
        val atomID = extractFirst(4).decodeToString()

        val size: Int
        val payload: ByteArray
        when (preliminarySize) {
            0 -> {
                // Here we get the remaining data and adjust the size accordingly
                payload = extractFirst(8)
                size = (8).toInt()
            }
            1 -> {
                size = extractToInt(8)
                payload = extractFirst(size - 16)
            }
            else -> {
                size = preliminarySize - 8
                payload = extractFirst(size)
            }
        }

        val identifier = AtomIdentifier.entries.firstOrNull { it.rawValue == atomID }
        if (identifier != null) {
            return identifier.parse(size, createNewInstance(payload))
        }

        return PassThrough(identifier = atomID, size = size, payload = createNewInstance(payload))
    }
}


class SimpleByteArrayReader(private val buffer: ByteArray, override val totalSize: Long, override val sizeAvailable: Long): ByteStreamReader() {
    override suspend fun extractFirst(n: Int): ByteArray {
        check(n > 0) {"n must be greater than zero. N: $n"}
        check(size >= n) {"Size must be greater than extract first bytes.. size was: $size n: $n"}

        val result = buffer.copyOfRange(cursor.toInt(), (cursor + n).toInt())
        cursor += n // Updating the cursor here to point to the next unread byte in the file
        return result
    }

    override suspend fun skip(n: Int) {
        check(n > 0)
        check(cursor + n < totalSize)
        cursor += n
    }

    override fun createNewInstance(data: ByteArray): ByteStreamReader {
        return SimpleByteArrayReader(data, data.size.toLong(), data.size.toLong())
    }

}

fun ByteArray.uInt64BE(index: Int = 0): ULong {
    var value = 0UL
    for (i in 0..7) {
        value = (value shl 8) or this[index + i].toUByte().toULong()
    }
    return value
}

fun ByteArray.uInt8BE(index: Int = 0): UByte {
    return this[index].toUByte()
}


// TODO this one will be defined platform specifically
//class FileByteStreamReader(filePath: String): ByteStreamReader() {
//
//
//    override val totalSize: Int
//        get() = TODO("Not yet implemented")
//
//    override suspend fun extractFirst(n: Int): ByteArray {
//        TODO("Not yet implemented")
//    }
//
//    override fun createNewInstance(data: ByteArray): ByteStreamReader {
//        TODO("Not yet implemented")
//    }
//
//}





//
//
//
//class ByteArrayReader( val data: ByteArray) {
//    private var cursor: Int = 0
//
//    val size: Int
//        get() = data.size - cursor
//
//    fun skipFirst(n: Int) {
//        if (n > data.size - cursor) {
//            throw IndexOutOfBoundsException("Not enough data to extract")
//        }
//        cursor += n
//    }
//
////    fun isNotEmpty(): Boolean = data.isNotEmpty()
////    fun isEmpty(): Boolean = data.isEmpty()
//
//    fun extractFirst(n: Int): ByteArray {
//        if (n > size) {
//            throw IndexOutOfBoundsException("Not enough data to extract")
//        }
//
//        val result = data.copyOfRange(cursor, cursor + n)
//        cursor += n
//        return result
//    }
//
//    fun extractToInt(n: Int): Int {
//        require(n in 1..4) { "Size should be between 1 and 4: $n" }
//        val extractedBytes = extractFirst(n)
//        var result = 0
//        for (i in 0..<n) {
//            result = result shl 8 or (extractedBytes[i].toInt() and 0xFF)
//        }
//        return result
//    }
//
//    fun extractToDouble(n: Int): Double {
//
//        if (n > data.size - cursor) {
//            throw IndexOutOfBoundsException("Not enough data to extract double")
//        }
//
//        val extractedBytes = extractFirst(n)
//        var longBits = 0L
//        for (i in 0..< n) {
//            longBits = longBits shl 8 or (extractedBytes[i].toLong() and 0xFF)
//        }
//        return Double.fromBits(longBits)
//    }
//
//    fun extractToUShort(): UShort {
//        val extractedBytes = extractFirst(2)
//        return ((extractedBytes[0].toInt() and 0xFF) shl 8 or (extractedBytes[1].toInt() and 0xFF)).toUShort()
//    }
//
//    fun extractToUInt(): UInt {
//        val extractedBytes = extractFirst(4)
//        return ((extractedBytes[0].toInt() and 0xFF) shl 24 or
//                (extractedBytes[1].toInt() and 0xFF) shl 16 or
//                (extractedBytes[2].toInt() and 0xFF) shl 8 or
//                (extractedBytes[3].toInt() and 0xFF)).toUInt()
//    }
//
//    fun extractToUByte(): UByte {
//        val extractedBytes = extractFirst(1)
//        return extractedBytes[0].toUByte()
//    }
//
//    fun extractAndParseToAtom(): Atom {
//        val preliminarySize = extractToInt(4)
//        val atomID = extractFirst(4).decodeToString()
//
//        val size: Int
//        val payload: ByteArray
//        when (preliminarySize) {
//            0 -> {
//                payload = data.copyOfRange(cursor, data.size)
//                size = data.size - cursor + 8
//            }
//            1 -> {
//                size = extractToInt(8)
//                payload = extractFirst(size - 16)
//            }
//            else -> {
//                size = preliminarySize
//                payload = extractFirst(size - 8)
//            }
//        }
//
//
//        val identifier = AtomIdentifier.entries.firstOrNull { it.rawValue == atomID }
//        if (identifier != null) {
//           return identifier.parse(size, payload)
//        }
//
//
//        return Atom(identifier = atomID, size = size, payload = ByteArrayReader(payload))
//    }
//
//
//    fun copyOf(): ByteArrayReader {
//        return ByteArrayReader(this.data.copyOf())
//    }
//}

fun ByteArray.toUByte(): UByte {
    require(isNotEmpty()) { "Array should not be empty" }
    return this[0].toUByte()
}

fun Long.toBytes(): ByteArray {
    return ByteArray(8) { i ->
        ((this shr (8 * i)) and 0xFF).toByte()
    }
}