package Atoms.SampleTableAtoms

import Atom
import Atoms.encode
import utils.ByteStreamReader



open class Stsd(
    identifier: String,
    size: Int,
    private val payload: ByteStreamReader,
    children: List<Atom> = listOf()
) : Atom(identifier, size, payload, children) {

    private lateinit var version: ByteArray
    private lateinit var flags: ByteArray
    var entryCount: Int = 0


//    constructor() : this(
//        "stsd",
//        16 + 16, // Assuming a fixed size for the Text atom here
//        ByteArrayReader(byteArrayOf(
//            *Atom.version,
//            *Atom.flags,
//            0, 0, 0, 1 // entryCount
//        )),
//        listOf(Atom("text", 16, emptyByteArrayReader())) // Creating a basic Atom instance as a placeholder for the Text atom
//    ) {
//        entryCount = 1
//    }

    override suspend fun parse(): Atom {
        version = payload.extractFirst(1)
        flags = payload.extractFirst(3)
        entryCount = payload.extractToInt(4)

        // Here, the parsing of child atoms would be done
        // The exact implementation depends on your existing structure
        // and how you have implemented the Atom class and its extensions
        return this
    }

    override val contentData: ByteArray
        get() {
            val reserve = size - 8
            var data = ByteArray(reserve)

            data += version
            data += flags
            data += entryCount.toUInt().toByteArray()
            data += children.flatMap { it.encode().toList() }.toByteArray()

            return data
        }
}

private fun UInt.toByteArray(): ByteArray {
    return byteArrayOf(
        (this shr 24).toByte(),
        (this shr 16).toByte(),
        (this shr 8).toByte(),
        this.toByte()
    )
}