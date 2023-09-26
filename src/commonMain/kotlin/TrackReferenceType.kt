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

    companion object {
        fun fromRawValue(rawValue: String): TrackReferenceType? {
            return entries.find { it.rawValue == rawValue }
        }
    }

    suspend fun parse(size: Int, payload: ByteStreamReader): Atom {
        return TrefSubatom(identifier = this.rawValue, size = size, payload = payload)
    }
}
