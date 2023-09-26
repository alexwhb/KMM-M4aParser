package Atoms.SampleTableAtoms

import Atom
import utils.ByteStreamReader


class Stsz : Atom {
    private lateinit var version: ByteArray
    private lateinit var flags: ByteArray
    var sampleSize: Int = 0
    var entryCount: Int = 0
    var sampleSizeTable: List<Int> = emptyList()
    private lateinit var payload: ByteStreamReader

    constructor(identifier: String, size: Int, payload: ByteStreamReader) : super(identifier, size, payload) {
        this.payload = payload

    }

    constructor(titles: List<String>) : super("stsz", calculateSize(titles)) {
        val sizes = titles.map { it.length + 2 }
        version = Atom.version
        flags = Atom.flags

        sampleSize = if (sizes.distinct().size == 1) {
            sizes.first()
        } else {
            0
        }
        sampleSizeTable = if (sampleSize == 0) sizes else emptyList()
        entryCount = sizes.size
    }

    override suspend fun parse(): Atom {
        version = payload.extractFirst(1)
        flags = payload.extractFirst(3)
        sampleSize = payload.extractToInt(4)
        entryCount = payload.extractToInt(4)

        sampleSizeTable = if (sampleSize == 0) {
            val entryList = mutableListOf<Int>()
            while (payload.size > 0 ) {
                entryList.add(payload.extractToInt(4))
            }
            entryList
        } else {
            emptyList()
        }
        return this
    }

    override val contentData: ByteArray
        get() {
            var data = ByteArray(0)
            data += version
            data += flags
            data += sampleSize.toUInt().toBytes()
            data += entryCount.toUInt().toBytes()
            data += sampleSizeTable.flatMap { it.toUInt().toBytes().toList() }.toByteArray()
            return data
        }

    companion object {
        private fun calculateSize(titles: List<String>): Int {
            val entryCount = titles.size
            return 20 + (entryCount * 4)
        }
    }
}

// TODO check if this already exists
fun UInt.toBytes(): ByteArray {
    val buffer = ByteArray(4)
    buffer[0] = ((this shr 24) and 0xFFu).toByte()
    buffer[1] = ((this shr 16) and 0xFFu).toByte()
    buffer[2] = ((this shr 8) and 0xFFu).toByte()
    buffer[3] = (this and 0xFFu).toByte()
    return buffer
}