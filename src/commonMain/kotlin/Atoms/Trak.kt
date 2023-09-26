package Atoms

import Atom
import enums.AtomIdentifier
import utils.ByteStreamReader


open class Trak : Atom {
    private lateinit var payload: ByteStreamReader

    constructor(identifier: String, size: Int, payload: ByteStreamReader) : super(identifier, size) {
        this.payload = payload
    }

    constructor(children: List<Atom>) : super("trak", 8 + children.sumOf { it.size }) {
        if (!children.any { it.identifier == "tkhd" }) {
            throw TkhdAtomNotFound
        }
        if (!children.any { it.identifier == "mdia" }) {
            throw MdiaAtomNotFound
        }

        super.children = children
    }

    override suspend fun parse(): Atom {
//        val data = payload.copyOf()
        val children = mutableListOf<Atom>()
        while (payload.size > 0) {
            payload.extractAndParseToAtom().let {
                children.add(it)
            }
        }

        if (!children.any { it.identifier == "tkhd" }) {
            throw TkhdAtomNotFound
        }
        if (!children.any { it.identifier == "mdia" }) {
            throw MdiaAtomNotFound
        }

        super.children = children
        return this
    }

    override val contentData: ByteArray
        get() {
            var data = ByteArray(children.sumOf { it.size })
            sortedAtoms.forEach { atom ->
                data += atom.encode() // assuming encode returns ByteArray
            }
            return data
        }

    private val sortedAtoms: List<Atom>
        get() = children.sortedBy { sortingGroup(it.identifier) }

    private fun sortingGroup(identifier: String) = when (identifier) {
        "tkhd" -> 1
        "tref" -> 2
        "mdia" -> 4
        else -> 3
    }


    val mdia: Mdia
        get() = this[AtomIdentifier.MDIA] as Mdia

    val tkhd: Tkhd
        get() = this[AtomIdentifier.TKHD] as Tkhd

}

sealed class TrakError(message: String) : Throwable(message)
object TkhdAtomNotFound : TrakError("Tkhd Atom Not Found")
object MdiaAtomNotFound : TrakError("Mdia Atom Not Found")

