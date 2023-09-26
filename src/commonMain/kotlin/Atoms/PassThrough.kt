package Atoms

import Atom
import utils.ByteStreamReader

class PassThrough(identifier: String, size: Int, payload: ByteStreamReader): Atom(identifier, size, payload) {
    override suspend fun parse(): Atom  {
        return this
    }
}
