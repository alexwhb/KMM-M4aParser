package enums

import Atom
import TrefSubatom
import utils.ByteStreamReader

enum class TrackReferenceType(val rawValue: String) {
    CDSC("cdsc"),
    CHAP("chap"),
    CLCP("clcp"),
    DPND("dpnd"),
    FALL("fall"),
    FOLW("folw"),
    FORC("forc"),
    HINT("hint"),
    IPIR("ipir"),
    MPOD("mpod"),
    SCPT("scpt"),
    SSRC("ssrc"),
    SYNC("sync"),
    TMCD("tmcd");
}

suspend fun TrackReferenceType.parse(size: Int, payload: ByteStreamReader): Atom {
    return TrefSubatom(identifier = this.rawValue, size = size, payload = payload).parse()
}