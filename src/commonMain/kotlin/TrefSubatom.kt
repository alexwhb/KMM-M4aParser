
import utils.ByteStreamReader

class TrefSubatom : Atom {
    var trackIDs: MutableList<Int>
    lateinit var payload: ByteStreamReader

    constructor(identifier: String, size: Int, payload: ByteStreamReader) : super(identifier, size) {
        trackIDs = mutableListOf()
        this.payload = payload
    }

    constructor(chapterTrackID: Int) : super("chap", 12) {
        trackIDs = mutableListOf(chapterTrackID)
    }

    override suspend fun parse():Atom {
        var index = 0
        while (index < payload.size) {
            trackIDs.add(payload.extractToInt(4))
            index += 4
        }
        return this
    }

    override val contentData: ByteArray
        get() {
            val byteArray = ByteArray(trackIDs.size * 4)
            var index = 0
            for (id in trackIDs) {
                byteArray.encodeInt(index, id)
                index += 4
            }
            return byteArray
        }

    private fun ByteArray.encodeInt(startIndex: Int, value: Int) {
        for (i in 0..3) {
            this[startIndex + i] = ((value shr (24 - i * 8)) and 0xFF).toByte()
        }
    }
}