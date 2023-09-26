package Atoms.SampleTableAtoms

import Atom

import utils.ByteStreamReader


class Stsc(
    identifier: String,
    size: Int,
    private val payload: ByteStreamReader
) : Atom(identifier, size, payload) {

    private lateinit var version: ByteArray
    private lateinit var flags: ByteArray
    var entryCount: Int = 0
    var sampleToChunkTable: List<SampleToChunkEntry> = emptyList()

    override suspend fun parse(): Atom {

        version = payload.extractFirst(1)
        flags = payload.extractFirst(3)
        entryCount = payload.extractToInt(4)
        sampleToChunkTable = mutableListOf()

        while (payload.size > 0) {
            val firstChunk = payload.extractToInt(4)
            val samplesPerChunk = payload.extractToInt(4)
            val sampleDescriptionID = payload.extractToInt(4)
            sampleToChunkTable += SampleToChunkEntry(firstChunk, samplesPerChunk, sampleDescriptionID)
        }
        return this
    }

//    constructor() : this(
//        "stsc",
//        28,
//        ByteArrayReader(
//            byteArrayOf(
//                *Atom.version,
//                *Atom.flags,
//                0, 0, 0, 1, // entryCount
//                0, 0, 0, 1, // defaultFirstChunk
//                0, 0, 0, 1, // defaultSamplesPerChunk
//                0, 0, 0, 1  // defaultDescriptionID
//            )
//        )
//    ) {
//        entryCount = 1
//        sampleToChunkTable = listOf(SampleToChunkEntry(1, 1, 1))
//    }
}

data class SampleToChunkEntry(val firstChunk: Int, val samplesPerChunk: Int, val sampleDescriptionID: Int)
