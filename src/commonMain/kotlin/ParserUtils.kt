

//fun ByteArray.extractAtomSize(): Int {
//    return this.sliceArray(0..<4).toInt()
//}
//
//fun ByteArray.extractAtomID(): String? {
//    val bytes = this.sliceArray(4..<8)
//    return bytes.toString()
//}
//
//
//fun ByteArray.extractAndParseToAtom(): Atom? {
//    val preliminarySize = this.extractAtomSize()
//    val atomID = this.extractAtomID() ?: return null
//
//    val size: Int
//    val payload: ByteArray
//    when (preliminarySize) {
//        0 -> {
//            payload = this
//            size = this.size + 8
//        }
//
//        1 -> {
//            size = this.sliceArray(8..<16).toInt()
//            payload = this.sliceArray(16..<(16 + size - 16))
//        }
//
//        else -> {
//            size = preliminarySize
//            payload = this.sliceArray(8..<(8 + size - 8))
//        }
//    }
//
//    return Atom(identifier = atomID, size = size, payload = ByteArrayReader(payload))
//}

fun ByteArray.toInt(): Int {
    var result = 0
    for (i in this.indices) {
        result = result shl 8
        result = result or (this[i].toInt() and 0xFF)
    }
    return result
}