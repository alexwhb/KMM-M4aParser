package Atoms

import Atom
import enums.TrackType
import utils.ByteStreamReader


open class Hdlr(identifier: String, size: Int, val payload: ByteStreamReader) : Atom(identifier, size) {

    private lateinit var version: ByteArray
    private lateinit var flags: ByteArray
    private lateinit var handlerTypeRaw: ByteArray
    private lateinit var handlerSubtypeRaw: ByteArray

    // TODO
//    lateinit var componentName: ByteArray


    val handlerSubtype: TrackType
        get() {
            var type = TrackType.UNKNOWN
            handlerSubtypeRaw.decodeToString().trimEnd('\u0000').let { typeString ->
                TrackType.entries.find { it.rawValue == typeString }?.let {
                    type = it
                }
            }
            check(type != TrackType.UNKNOWN) { "Unrecognized handler subtype" }
            return type
        }

    override suspend fun parse(): Atom {
        version = payload.extractFirst(1)
        flags = payload.extractFirst(3)
        handlerTypeRaw = payload.extractFirst(4)
        handlerSubtypeRaw = payload.extractFirst(4)
        // reserved
        payload.extractFirst(12)
//        componentName = payload.data
        return this
    }

}
