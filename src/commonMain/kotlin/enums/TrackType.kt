package enums

enum class TrackType(val rawValue: String) {
    VIDE("vide"),
    SOUN("soun"),
    TEXT("text"),
    SUBT("subt"),
    META("meta"),
    TMCD("tmcd"),
    CLCP("clcp"),
    SBTL("sbtl"),
    MUSI("musi"),
    MPEG("MPEG"),
    SPRT("sprt"), // deprecated
    TWEN("twen"), // deprecated
    TX3G("tx3g"),
    UNKNOWN("unknown");
}