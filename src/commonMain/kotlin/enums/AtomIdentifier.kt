package enums

import Atom
import Atoms.*
import Atoms.SampleTableAtoms.*
import utils.ByteStreamReader

enum class AtomIdentifier(val rawValue: String) {
    AC3("ac-3"),
    AMR("amr "),
    ALAC("alac"),
    AVC1("avc1"),
    AVCC("avcC"),
    BITR("bitr"),
    BTRT("btrt"),
    BURL("burl"),
    CHPL("chpl"),
    CO64("co64"),
    COLR("colr"),
    CTRY("ctry"),
    CTTS("ctts"),
    D263("d263"),
    DAC3("dac3"),
    DAMR("damr"),
    DATA("data"),
    DIMM("dimm"),
    DINF("dinf"),
    DMAX("dmax"),
    DMED("dmed"),
    DREF("dref"),
    DREP("drep"),
    EDTS("edts"),
    ELNG("elng"),
    ELST("elst"),
    ENCA("enca"),
    ENCV("encv"),
    ESDS("esds"),
    FREE("free"),
    FRMA("frma"),
    FTAB("ftab"),
    FTYP("ftyp"),
    GMHD("gmhd"),
    GMIN("gmin"),
    HDLR("hdlr"),
    HMHD("hmhd"),
    HNTI("hnti"),
    HREF("href"),
    IKMS("iKMS"),
    IMIF("imif"),
    ILST("ilst"),
    IODS("iods"),
    ISFM("iSFM"),
    KEYS("keys"),
    LANG("lang"),
    LOAD("load"),
    MAXR("maxr"),
    MDAT("mdat"),
    MDHD("mdhd"),
    MDIA("mdia"),
    MEAN("mean"),
    META("meta"),
    MFHD("mfhd"),
    MHDR("mhdr"),
    MINF("minf"),
    MOOF("moof"),
    MOOV("moov"),
    MP4A("mp4a"),
    MP4S("mp4s"),
    MP4V("mp4v"),
    MVEX("mvex"),
    MVHD("mvhd"),
    NAME("name"),
    NMHD("nmhd"),
    NUMP("nump"),
    ODKM("odkm"),
    OHDR("ohdr"),
    PASP("pasp"),
    PAYT("payt"),
    PINF("pinf"),
    PMAX("pmax"),
    PNOT("pnot"),
    PTV("ptv "),
    RTP("rtp "),
    S263("s263"),
    SAMR("samr"),
    SAWB("sawb"),
    SBGP("sbgp"),
    SCHI("schi"),
    SCHM("schm"),
    SDP("sdp "),
    SDTP("sdtp"),
    SGPD("sgpd"),
    SINF("sinf"),
    SKIP("skip"),
    SMHD("smhd"),
    SNRO("snro"),
    STBL("stbl"),
    STCO("stco"),
    STDP("stdp"),
    STSC("stsc"),
    STSD("stsd"),
    STSH("stsh"),
    STSS("stss"),
    STSZ("stsz"),
    STTS("stts"),
    STZ2("stz2"),
    TAGC("tagc"),
    TEXT("text"),
    TFHD("tfhd"),
    TGAS("tgas"),
    TIMS("tims"),
    TKHD("tkhd"),
    TMAX("tmax"),
    TMIN("tmin"),
    TNAM("tnam"),
    TPYL("tpyl"),
    TRAF("traf"),
    TRAK("trak"),
    TREF("tref"),
    TREX("trex"),
    TRPY("trpy"),
    TRUN("trun"),
    TSRO("tsro"),
    TX3G("tx3g"),
    UDTA("udta"),
    URN("urn "),
    VMHD("vmhd"),
    WAVE("wave"),
    WIDE("wide");

    suspend fun parse(size: Int, payload: ByteStreamReader): Atom {
        check(size > 0) {"The size of the payload must be greater than zero bytes. size: $size id: ${this.rawValue}"}
        return when(this) {
            TRAK -> {
                Trak(rawValue, size, payload).parse()
            }
            MOOV -> {
                Moov(rawValue, size, payload).parse()
            }
            TKHD -> {
                Tkhd(rawValue, size, payload).parse()
            }
            MINF -> {
                Minf(rawValue, size, payload).parse()
            }
            MDIA -> {
                Mdia(rawValue, size, payload).parse()
            }
            HDLR -> {
                Hdlr(rawValue, size, payload).parse()
            }
            STBL -> {
                Stbl(rawValue, size, payload).parse()
            }
            STSC -> {
                Stsc(rawValue, size,payload).parse()
            }
            STSD -> {
                Stsd(rawValue, size, payload).parse()
            }
            STSZ -> {
                Stsz(rawValue, size, payload).parse()
            }
            STTS -> {
                Stts(rawValue, size, payload).parse()
            }
            MDHD -> {
                Mdhd(rawValue, size, payload).parse()
            }
            STCO, CO64 -> {
                ChunkOffsetAtom(rawValue, size, payload).parse()
            }
            TEXT -> {
                Text(rawValue, size, payload).parse()
            }

            else -> {
                PassThrough(this.rawValue, size, payload).parse()
            }
        }
    }

}