package Atoms.SampleTableAtoms

import Atom
import utils.*

/*
The atom contains a compact version of a table that allows indexing from time to sample number.
Other tables provide sample sizes and pointers from the sample number. Each entry in the table gives
the number of consecutive samples with the same time delta, and the delta of those samples.
By adding the deltas, a complete time-to-sample map can be built.

 The atom contains time deltas: DT(n+1) = DT(n) + STTS(n) where STTS(n) is the (uncompressed) table entry for
 sample n and DT is the display time for sample (n). The sample entries are ordered by time stamps; therefore,
 the deltas are all nonnegative. The DT axis has a zero origin; DT(i) = SUM (for j=0 to i-1 of delta(j)), and
 the sum of all deltas gives the length of the media in the track (not mapped to the overall time scale, and not
 considering any edit list). The edit list atom provides the initial DT value if it is nonempty (nonzero).

 In chaptering terms, this describes the duration of each chapter


 A class representing a `stts` atom in an `Mp4File`'s atom structure
 This atom maps the duration of each sample to a display time (start time) in the media

 */

class Stts(identifier: String, size: Int, private val payload: ByteStreamReader) : Atom(identifier, size, payload) {

    private lateinit var version: ByteArray
    private lateinit var flags: ByteArray
    var entryCount: Int = 0
    lateinit var sampleTable: List<Pair<Int, Double>>


    override suspend fun parse(): Atom {
        version = payload.extractFirst(1)
        flags = payload.extractFirst(3)
        entryCount = payload.extractToInt(4)
        val entryList = mutableListOf<Pair<Int, Double>>()
        while (payload.size > 0 ) {
            val sampleCount = payload.extractToInt(4)
            val sampleDuration = payload.extractToDouble(4)
            entryList.add(Pair(sampleCount, sampleDuration))
        }
        sampleTable = entryList
        return this
    }

//    constructor(chapterHandler: ChapterHandler, mediaDuration: Double) : super("stts", calculateSize(entries)) {
//        val durationArray = chapterHandler.calculateDurationsFromStartTimes(mediaDuration)
//
//        val entries = mutableListOf<Pair<Int, Double>>()
//        var currentDuration = durationArray.firstOrNull() ?: throw StblError.SampleDurationArrayIsEmpty
//        var count = 1
//
//        for (duration in durationArray.drop(1)) {
//            if (duration == currentDuration) {
//                count++
//            } else {
//                entries.add(Pair(count, currentDuration))
//                currentDuration = duration
//                count = 1
//            }
//        }
//        entries.add(Pair(count, currentDuration))
//
//        sampleTable = entries
//        entryCount = entries.size
//        version = Atom.version
//        flags = Atom.flags
//    }

    override val contentData: ByteArray
        get() {
            var data = ByteArray(0)
            data += version
            data += flags
            data += entryCount.toUInt().toBytes()
            for (entry in sampleTable) {
                data += entry.first.toUInt().toBytes()
                data += entry.second.toBits().toBytes()
            }
            return data
        }

    companion object {
        private fun calculateSize(entries: List<Pair<Int, Double>>): Int {
            return 16 + (8 * entries.size)
        }
    }
}



