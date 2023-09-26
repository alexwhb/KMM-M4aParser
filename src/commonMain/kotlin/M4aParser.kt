import Atoms.Moov
import enums.AtomIdentifier
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import utils.ByteStreamReader

class M4aParser(val data: ByteStreamReader) {

    var rootAtoms: MutableList<Atom> = mutableListOf()


    suspend fun parse() {
        val atoms: MutableList<Atom> = mutableListOf()

        while (data.size > 0) {
            val atom: Atom = data.extractAndParseToAtom()
            atoms.add(atom)
            // we should break after the Moov Atom has been parsed in order to not
            // fetch more data than we need.
            if (atom is Moov) {
                break
            }
        }
        this.rootAtoms = atoms

//        // Replace with actual calls to moov, soundTrack, etc., properties
//        // I am assuming these are properties of type Atom that you'll define
//        if (moov.soundTrack.mdia.minf.stbl.chunkOffsetAtom.identifier == "co64") {
//            use64BitOffset = true
//        }

//        this.chunkSizes = chunkSizes(stbl = moov.soundTrack.mdia.minf.stbl)
    }



    val moov: Moov
        get() = rootAtoms.first { it.identifier == AtomIdentifier.MOOV.rawValue} as Moov


    // rest of your properties, methods here including `moov`, `mdats`, `duration`, etc.
}

sealed class Mp4FileError : Throwable() {

    object InvalidFileFormat : Mp4FileError()
    object OutputFailure : Mp4FileError()
    object UnableToInitializeAtomsFromFileData : Mp4FileError()
    class UnableToInitializeRequiredAtom(val atomIdentifier: String) : Mp4FileError()
    object MoovAtomNotFound : Mp4FileError()
    object MdatAtomNotFound : Mp4FileError()
    object MissingSample : Mp4FileError()
    object MissingChunk : Mp4FileError()
    object ChunkSizeToChunkOffsetCountMismatch : Mp4FileError()
    object NewChunkOffsetArrayCountMismatch : Mp4FileError()
    object ChapterHandlerMissing : Mp4FileError()

    // Here, you can override the 'message' property of Throwable to provide a custom error message
    override val message: String?
        get() = when (this) {
            InvalidFileFormat -> "The file is not an MP4 format audio file."
            OutputFailure -> "Writing operation failed."
            UnableToInitializeAtomsFromFileData -> "Atoms failed to initialize."
            is UnableToInitializeRequiredAtom -> "Unable to initialize required atom: $atomIdentifier."
            MoovAtomNotFound -> "Required root atom 'moov' is missing."
            MdatAtomNotFound -> "Required root atom 'mdat' is missing."
            MissingSample -> "Samples cannot be located."
            MissingChunk -> "Media chunk cannot be located."
            ChunkSizeToChunkOffsetCountMismatch -> "Entry count of the chunkSizes array does not match the count of the chunkOffsets array."
            NewChunkOffsetArrayCountMismatch -> "The new chunk offsets array doesn't match the old chunk offsets array."
            ChapterHandlerMissing -> "Chapter handler is missing."
        }
}