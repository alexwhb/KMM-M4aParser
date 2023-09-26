package Atoms

import Atom
import enums.AtomIdentifier
import enums.TrackType
import utils.ByteStreamReader


class Moov : Atom {

    private lateinit var payload: ByteStreamReader

    constructor(identifier: String, size: Int, payload: ByteStreamReader) : super(identifier, size, payload) {
        this.payload = payload
    }


    @Throws(MoovError::class)
    constructor(children: List<Atom>) : super("moov", children.sumOf { it.size } + 8, children) {
        if (children.none { it.identifier == "mvhd" }) throw MvhdAtomNotFound
        val tracks = children.filterIsInstance<Trak>().filter { it.mdia.hdlr.handlerSubtype == TrackType.SOUN }
        if (tracks.isEmpty()) throw TrakAtomNotFound
    }

    private fun sortingGroup(forIdentifier: String): Int {
        return when (forIdentifier) {
            "mvhd" -> 1
            "trak" -> 2
            else -> 3
        }
    }

    private fun trackSorting(forTrackType: TrackType): Double {
        return when (forTrackType) {
            TrackType.SOUN -> 2.1
            TrackType.TEXT -> 2.2
            else -> 2.3
        }
    }

    val sortedAtoms: List<Atom>
        get() {
            val tracks = this.children.filterIsInstance<Trak>()
            tracks.sortedBy { trackSorting(it.mdia.hdlr.handlerSubtype) }
            this.children = tracks
            return this.children.sortedBy { sortingGroup(it.identifier) }
        }

    override suspend fun parse(): Atom {
        val children = mutableListOf<Atom>()
        // Assuming data is a class that represents a byte array and has `isEmpty`,
        // `extractAndParseToAtom` etc. methods. Adjust this based on your actual data class implementation
//        val data = Data(payload)
        while (payload.size > 0) {
            payload.extractAndParseToAtom().let {
                children.add(it)
            }
        }

        if (children.none { it.identifier == "mvhd" }) throw MvhdAtomNotFound
        val tracks =
            children.filter { it.identifier == "trak" && it is Trak && it.mdia.hdlr.handlerSubtype == TrackType.SOUN }
        if (tracks.isEmpty()) throw TrakAtomNotFound

        super.children = children
        return this
    }

    override val contentData: ByteArray
        get() {
            // Here you need to implement the way to get byte array representation
            // of the atoms based on your requirements, I used pseudo function `encode`
            val reserve = children.sumOf { it.size }
            var data = ByteArray(reserve)
            for (atom in sortedAtoms) {
                data += atom.encode()
            }
            return data
        }


    val soundTrack: Trak
        get() {
//            val atom = (this[AtomIdentifier.TRAK] as? Trak)
//            if (atom?.)

//            if let atom = self[.trak] as? Trak,
//            atom.mdia.hdlr.handlerSubtype == .soun {
//                return atom
//            } else {
//                fatalError("Required child 'trak' is missing from string metadata atom with identifier '\(self.identifier)'")
//            }
            TODO("")
        }

    val tracks: List<Trak>
        get() = children.filter { it.identifier == AtomIdentifier.TRAK.rawValue } as? List<Trak> ?: emptyList()



}


sealed class MoovError(message: String) : Throwable(message)

object MvhdAtomNotFound : MoovError("Mvhd Atom Not Found")
object TrakAtomNotFound : MoovError("Trak Atom Not Found")


// Data class representing byte array and has utility functions
class Data(private val payload: ByteArray) {
    fun isEmpty() = payload.isEmpty()
    fun extractAndParseToAtom(): Atom? {
        // Implement this based on your requirement
        return null
    }
}

fun Atom.encode(): ByteArray {
    // Implement this to convert Atom to ByteArray based on your requirement
    return byteArrayOf()
}