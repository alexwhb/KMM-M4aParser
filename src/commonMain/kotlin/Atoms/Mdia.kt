package Atoms

import Atom
import enums.AtomIdentifier
import utils.ByteStreamReader


open class Mdia : Atom {

    private lateinit var payload: ByteStreamReader
    constructor(identifier: String, size: Int, payload: ByteStreamReader) : super(identifier, size) {
        this.payload = payload
    }

    private constructor(children: List<Atom>) : super("mdia", children.sumOf { it.size } + 8, children) {
        checkRequiredAtoms(children)
    }

    override suspend fun parse(): Atom {
        super.children = parseChildren(payload)
        checkRequiredAtoms(children)
        return this
    }

    private suspend fun parseChildren(payload: ByteStreamReader): List<Atom> {
        val children = mutableListOf<Atom>()

        while (payload.size > 0) {
            val child = payload.extractAndParseToAtom()
            children.add(child)
        }

        return children
    }

    private fun checkRequiredAtoms(children: List<Atom>) {
        val identifiers = children.map { it.identifier }
        if ("mdhd" !in identifiers) throw MdhdAtomNotFound
        if ("hdlr" !in identifiers) throw HdlrAtomNotFound
        if ("minf" !in identifiers) throw MinfAtomNotFound
    }

    val sortedAtoms: List<Atom>
        get() = children.sortedBy { sortingGroup(it.identifier) }

//    override val contentData: ByteArray
//        get() {
//            val data = ByteArray(children.sumOf { it.size })
//            sortedAtoms.flatMap { it.encode() }.toByteArray()
//            return data
//        }

    private fun sortingGroup(identifier: String) = when (identifier) {
        "mdhd" -> 1
        "hdlr" -> 2
        "minf" -> 3
        else -> 4
    }


    val hdlr: Hdlr
        get() = this[AtomIdentifier.HDLR] as Hdlr

    val minf
        get() = this[AtomIdentifier.MINF] as Minf

    val mdhd: Mdhd
        get() =  this[AtomIdentifier.MDHD] as Mdhd

}

sealed class MdiaError(message:String): Throwable(message)
object MdhdAtomNotFound: MdiaError("Mdhd Atom Not Found")
object HdlrAtomNotFound: MdiaError("Hdlr Atom Not Found")
object MinfAtomNotFound: MdiaError("Minf Atom Not Found")

