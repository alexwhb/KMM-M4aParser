package Atoms

import Atom
import utils.*

// TODO more to do here.

open class Tkhd(identifier: String, size: Int, val payload: ByteStreamReader) : Atom(identifier, size) {

    private lateinit var version: ByteArray
    private lateinit var flags: ByteArray
    var trackID: Int = -1
    private lateinit var durationRaw: ByteArray
    var layer: UShort = 0.toUShort()
    var alternateGroup: UShort = 0.toUShort()
    var volume: UShort = 0.toUShort()
    var trackWidth: UInt = 0.toUInt()
    var trackHeight: UInt = 0.toUInt()

    val duration: Double
        get() {
            // Your existing logic for computing duration goes here.
            // Note that Swift's optional type (`Type?`) is represented in Kotlin as `Type?`
            // ...
            return 0.0
        }

//    constructor(mediaDuration: Double, trackID: Int) : super("tkhd", calculateSize()) {
//        version = Atom.version
//        flags = Atom.flags
//
//        this.trackID = trackID
//
//        // Duration conversion logic goes here
//        // ...
//
//        layer = 0U
//        alternateGroup = 0U
//        volume = 0U
//        trackWidth = 0U
//        trackHeight = 0U
//
//        // Further initialization
//        // ...
//    }

    companion object {
        fun calculateSize(): Int {
            // Logic for calculating size goes here
            // ...
            return 0
        }
    }

    override suspend fun parse(): Atom {

        version = payload.extractFirst(1)
        flags = payload.extractFirst(3)

        val creationTime: Int
        val modificationTime: Int
        if (version.toUByte() == 0x01.toUByte()) {
            creationTime = payload.extractToInt(8)
            modificationTime = payload.extractToInt(8)
        } else {
            creationTime = payload.extractToInt(4)
            modificationTime = payload.extractToInt(4)
        }

        trackID = payload.extractToInt(4)

        // Skipping 4 bytes
        payload.skip(4)

        durationRaw = if (version.toUByte() == 0x01.toUByte()) {
            payload.extractFirst(8)
        } else {
            payload.extractFirst(4)
        }

        // Skipping 8 bytes
        payload.skip(8)

        layer = payload.extractToUShort()
        alternateGroup = payload.extractToUShort()
        volume = payload.extractToUShort()

        // Skipping 2 bytes
        payload.skip(2)

        val matrixStructure = payload.extractFirst(36)
        trackWidth = payload.extractToUInt()
        trackHeight = payload.extractToUInt()

        return this
    }
}

