package Atoms.SampleTableAtoms

import Atom
import enums.AtomIdentifier
import utils.ByteStreamReader
import kotlin.math.min


open class Stbl(identifier: String, size: Int, children: List<Atom> = emptyList()) : Atom(identifier, size, children) {

    private lateinit var payload: ByteStreamReader

    constructor(identifier: String, size: Int, payload: ByteStreamReader) : this(identifier, size) {
        this.payload = payload
    }


    override suspend fun parse(): Atom {
        val children = mutableListOf<Atom>()
        while (payload.size > 0) {
            val child = payload.extractAndParseToAtom()
            children.add(child)
        }

        // Validation checks
        if (!children.any { it.identifier == "stsd" }) throw StsdAtomNotFound
        if (!children.any { it.identifier == "stsc" }) throw StscAtomNotFound
        if (!children.any { it.identifier == "stts" }) throw SttsAtomNotFound
        if (!children.any { it.identifier == "stsz" }) throw StszAtomNotFound
        if (!children.any { it.identifier == "c064" || it.identifier == "stco" }) throw ChunkOffsetAtomNotFound
        this.children = children
        return this
    }
    // Other functions and initializers...

    val sortedAtoms: List<Atom>
        get() {
            val rearrangedAtoms = children.toMutableList()
            rearrangedAtoms.sortBy { sortingGroup(it.identifier) }
            return rearrangedAtoms
        }

    private fun sortingGroup(identifier: String): Int {
        return when (identifier) {
            "stsd" -> 1
            "stsc" -> 2
            "stts" -> 3
            "stsz" -> 4
            else -> 5
        }
    }

    val stsc: Stsc
        get() = this[AtomIdentifier.STSC] as Stsc

    val stsd: Stsd
        get() = this[AtomIdentifier.STSD] as Stsd

    val stsz: Stsz
        get() = this[AtomIdentifier.STSZ] as Stsz

    val stts: Stts
        get() = this[AtomIdentifier.STTS] as Stts

    val chunkOffsetAtom: ChunkOffsetAtom
        get() = this[AtomIdentifier.CO64] as? ChunkOffsetAtom ?: this[AtomIdentifier.STCO] as ChunkOffsetAtom


    fun findPacketForTime(timeInSeconds: Double, timeScale: Double): Int {
        val (_, sampleDuration) = stts.sampleTable.first()

        val totalTicks = timeInSeconds * timeScale
        return (totalTicks / sampleDuration).toInt()
    }


    fun packetByteOffsetNew(sampleIndex: Int): Int? {
        val stszSampleSizes = stsz.sampleSizeTable

        var count = 0

        stszSampleSizes
            .take(sampleIndex)
            .forEach { bytes ->
                count += bytes
            }
        return count
    }


    fun packetByteOffset(sampleIndex: Int): Int? {
        // Find the chunk that contains the sample
        var currentSampleIndex = 0
        var currentChunkIndex = 0
        var entryIndex = 0

        val stcoOffsets = chunkOffsetAtom.chunkOffsetTable
        val sampleChunkTable = stsc.sampleToChunkTable
        val stszSampleSizes = stsz.sampleSizeTable

        while (entryIndex < sampleChunkTable.size) {
            val nextFirstChunk =
                if (entryIndex + 1 < sampleChunkTable.size) sampleChunkTable[entryIndex + 1].firstChunk - 1 else Int.MAX_VALUE
            val samplesPerChunk = sampleChunkTable[entryIndex].samplesPerChunk

            while (currentChunkIndex < nextFirstChunk) {
                val chunkSampleRange = currentSampleIndex..<currentSampleIndex + samplesPerChunk
                if (sampleIndex in chunkSampleRange) {
                    break
                }

                currentSampleIndex += samplesPerChunk
                currentChunkIndex += 1
            }

            if (currentChunkIndex < nextFirstChunk) {
                break
            }

            entryIndex += 1
        }

        if (currentChunkIndex >= stcoOffsets.size) {
            return null
        }

        // Get the byte offset of the start of the chunk
        val chunkOffset = stcoOffsets[currentChunkIndex].toInt()

        // Find the sample's offset within the chunk
        val sampleOffsetWithinChunk = stszSampleSizes.slice(currentSampleIndex..<sampleIndex).sum()

        // Calculate the final byte offset of the sample in the file
        val sampleByteOffset = chunkOffset + sampleOffsetWithinChunk

        return sampleByteOffset
    }


    fun packetIndexFromByteOffset(byteOffset: Int): Int? {
        var currentChunkIndex = 0
        var currentSampleIndex = 0
        var entryIndex = 0

        val stcoOffsets = chunkOffsetAtom.chunkOffsetTable
        val sampleChunkTable = stsc.sampleToChunkTable
        val stszSampleSizes = stsz.sampleSizeTable

        while (currentChunkIndex < stcoOffsets.size) {
            val chunkOffset = stcoOffsets[currentChunkIndex].toInt()

            val samplesPerChunk =
                if (entryIndex < sampleChunkTable.size) sampleChunkTable[entryIndex].samplesPerChunk
                else return null // or continue based on your data structure

            val nextFirstChunk =
                if (entryIndex + 1 < sampleChunkTable.size) sampleChunkTable[entryIndex + 1].firstChunk
                else Int.MAX_VALUE

            val toIndex = min(currentSampleIndex + samplesPerChunk, stszSampleSizes.size)
            val chunkLastByteOffset = chunkOffset + stszSampleSizes.subList(currentSampleIndex, toIndex).sum()

            if (byteOffset < chunkLastByteOffset) {
                val sampleSizesWithinChunk = stszSampleSizes.subList(currentSampleIndex, toIndex)
                var sampleOffsetWithinChunk = 0

                for ((index, size) in sampleSizesWithinChunk.withIndex()) {
                    if (byteOffset < chunkOffset + sampleOffsetWithinChunk + size) {
                        return currentSampleIndex + index
                    }
                    sampleOffsetWithinChunk += size
                }
            }

            currentSampleIndex += samplesPerChunk
            currentChunkIndex += 1

            if (currentChunkIndex >= nextFirstChunk) entryIndex += 1
        }

        return null
    }


    fun findPacketIndexFromConsecutiveSampleLengths(sampleLengths: List<Int>): Int? {
        val stszSampleSizes = stsz.sampleSizeTable

        for (i in 0 until stszSampleSizes.size) {
            if (i + sampleLengths.size > stszSampleSizes.size) break // avoid IndexOutOfBoundsException

            var isMatch = true
            for (j in sampleLengths.indices) {
                if (stszSampleSizes[i + j] != sampleLengths[j]) {
                    isMatch = false
                    break
                }
            }

            if (isMatch) return i + sampleLengths.size - 1
        }

        return null
    }



    // I won't convert getChapterTitlesFromOffsetsAndSizes & other methods because they use methods/properties that aren't provided.
}

sealed class StblError(message: String) : Throwable(message)

object StsdAtomNotFound : StblError("Stsd atom not found")
object StscAtomNotFound : StblError("Stsc atom not found")
object ChunkOffsetAtomNotFound : StblError("Chunk offset atom not found")
object StszAtomNotFound : StblError("Stsz atom not found")
object SttsAtomNotFound : StblError("Stts atom not found")