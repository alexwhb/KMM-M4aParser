package Atoms

import Atom
import Atoms.SampleTableAtoms.Stbl
import enums.AtomIdentifier
import utils.ByteStreamReader

class Minf(identifier: String, size: Int, val payload: ByteStreamReader): Atom(identifier, size, payload) {

    override suspend fun parse(): Atom {
        val children = mutableListOf<Atom>()
        while (payload.size > 0) {
            val child = payload.extractAndParseToAtom()
            children.add(child)
        }
        this.children = children
        return this
    }

    private fun validateChildren(children: List<Atom>) {
        val requiredIdentifiers = listOf("nmhd", "gmhd", "smhd", "vmhd", "dinf", "stbl")
        for (id in requiredIdentifiers) {
            if (children.none { it.identifier == id }) {
                when (id) {
                    "nmhd", "gmhd", "smhd", "vmhd" -> throw MinfError.MediaInformationHeaderAtomNotFound
                    "dinf" -> throw MinfError.DinfAtomNotFound
                    "stbl" -> throw MinfError.StblAtomNotFound
                }
            }
        }
    }

    private fun sortingGroup(forIdentifier: String): Int {
        return when (forIdentifier) {
            "nmhd", "gmhd", "smhd", "vmhd" -> 1
            "dinf" -> 2
            "stbl" -> 3
            else -> 4
        }
    }

    val sortedAtoms: List<Atom>
        get() {
            return children.sortedBy { sortingGroup(it.identifier) }
        }


    val stbl: Stbl
        get() = this[AtomIdentifier.STBL] as Stbl



//    val contentData: Data
//        get() {
//            // Placeholder logic to create and return a Data object with contents
//            val data = Data()
//            sortedAtoms.forEach { /* data.append(it.encode()) */ }
//            return data
//        }

    private sealed class MinfError : Throwable() {
        object MediaInformationHeaderAtomNotFound : MinfError()
        object DinfAtomNotFound : MinfError()
        object StblAtomNotFound : MinfError()
    }


}