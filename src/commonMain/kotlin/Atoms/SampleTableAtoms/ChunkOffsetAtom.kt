package Atoms.SampleTableAtoms

import Atom
import utils.ByteStreamReader


open class ChunkOffsetAtom(
    identifier: String,
    size: Int,
    private val payload: ByteStreamReader
) : Atom(identifier, size, payload) {

    private lateinit var version: ByteArray // + 1
    private lateinit var flags: ByteArray // + 3
    var entryCount: Int = 0 // + 4
    lateinit var chunkOffsetTable: List<Int>

    override suspend fun parse(): Atom {
        this.version = payload.extractFirst(1)
        this.flags = payload.extractFirst(3)
        this.entryCount = payload.extractToInt(4)

        val offsetTable = mutableListOf<Int>()

        // If the identifier is co64, the offsets are stored as 64-bit integers
        while (payload.size > 0) {
            if (identifier == "co64") {
                offsetTable.add(payload.extractToInt(8))
            } else {
                offsetTable.add(payload.extractToInt(4))
            }
        }
        this.chunkOffsetTable = offsetTable
        return this
    }

    // Additional constructors and methods can be added here

//    override val contentData: ByteArray
//        get() {
//            val reserve = size - 8
//
//            val data = ByteArray(reserve)
//
//            data.plus(version)
//                .plus(flags)
//                .plus(entryCount.toUInt().toByteArray())
//            chunkOffsetTable.forEach { offset ->
//                if (identifier == "co64") {
//                    data.plus(offset.toULong().toByteArray())
//                } else {
//                    data.plus(offset.toUInt().toByteArray())
//                }
//            }
//            return data
//        }
}