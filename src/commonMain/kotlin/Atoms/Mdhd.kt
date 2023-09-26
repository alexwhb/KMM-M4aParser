package Atoms

import Atom
import utils.ByteStreamReader
import utils.uInt64BE
import utils.uInt8BE

class Mdhd(identifier: String, size: Int, payload: ByteStreamReader) : Atom(identifier, size) {
    private lateinit var version: ByteArray
    private lateinit var flags: ByteArray
    var timeScale: Double = 0.0
    var duration: Double = 0.0
    private var languageUInt16: UShort = 0.toUShort()
    var quality: Int = 0

    val durationSec: Double
        get() = duration/timeScale

    var creationTime: UInt =
        0.toUInt() // Seems to be missing in the original class but was used in the constructor. Added here for completeness
    var modificationTime: UInt = 0.toUInt() // Same as above

    private val payload: ByteStreamReader

    init {
        this.payload = payload
    }

    override suspend fun parse(): Atom {
        version = payload.extractFirst(1)
        flags = payload.extractFirst(3)

        val creationTime: UInt
        val modificationTime: UInt

        if (version.uInt8BE() == 0x01.toUByte()) {
            creationTime = payload.extractFirst(8).uInt64BE().toUInt()
            modificationTime = payload.extractFirst(8).uInt64BE().toUInt()
        } else {
            creationTime = payload.extractToUInt()
            modificationTime = payload.extractToUInt()
        }

        timeScale = payload.extractToDouble(4)
        duration = if (version.uInt8BE() == 0x01.toUByte()) {
            payload.extractToDouble(8)
        } else {
            payload.extractToDouble(4)
        }

        languageUInt16 = payload.extractToUShort()
        quality = payload.extractToInt(2)

        this.creationTime = creationTime
        this.modificationTime = modificationTime
        return this
    }

}