package Atoms.SampleTableAtoms

import Atom
import utils.ByteStreamReader


//import Atom
//import utils.ByteArrayReader
//
//import utils.toUShort

class Text(identifier: String, size: Int, private val payload: ByteStreamReader) : Atom(identifier, size, payload) {

    var dataReferenceIndex: UShort = 0.toUShort()
    var displayFlags: UInt = 0.toUInt()
    var textJustification: UInt = 0.toUInt()
    var bgColorRed: UShort = 0.toUShort()
    var bgColorGreen: UShort = 0.toUShort()
    var bgColorBlue: UShort = 0.toUShort()
    var defineTextBoxTop: UShort = 0.toUShort()
    var defineTextBoxLeft: UShort = 0.toUShort()
    var defineTextBoxBottom: UShort = 0.toUShort()
    var defineTextBoxRight: UShort = 0.toUShort()
    var fontID: UShort = 0.toUShort()
    var fontFace: UShort = 0.toUShort()
    var fontColorRed: UShort = 0.toUShort()
    var fontColorGreen: UShort = 0.toUShort()
    var fontColorBlue: UShort = 0.toUShort()


    override suspend fun parse(): Atom {
        payload.skip(6)
        dataReferenceIndex = payload.extractToUShort()
        displayFlags = payload.extractToUInt()
        textJustification = payload.extractToUInt()
        bgColorRed = payload.extractToUShort()
        bgColorGreen = payload.extractToUShort()
        bgColorBlue = payload.extractToUShort()
        defineTextBoxTop = payload.extractToUShort()
        defineTextBoxLeft = payload.extractToUShort()
        defineTextBoxBottom = payload.extractToUShort()
        defineTextBoxRight = payload.extractToUShort()
        payload.skip(8)
        fontID = payload.extractToUShort()
        fontFace = payload.extractToUShort()
        payload.skip(3)
        fontColorRed = payload.extractToUShort()
        fontColorGreen = payload.extractToUShort()
        fontColorBlue = payload.extractToUShort()
        return this
    }

//    constructor() : this(
//        "text",
//        59,
//        ByteArrayReader(byteArrayOf()) // You might want to provide a proper payload here
//    ) {
//        this.dataReferenceIndex = 0x0001u
//        this.displayFlags = 0x00000001u
//        this.textJustification = 0x00000001u
//        // ... Continue to initialize other properties in a similar fashion
//    }

//    override val contentData: ByteArray
//        get() {
//            val data = ByteArray(0) // Initialize with the correct capacity
//            data.appendReserveData(6)
//            data.append(this.dataReferenceIndex.toBytesBE())
//            data.append(this.displayFlags.toBytesBE())
//            // ... Continue to append other properties in a similar fashion
//            return data
//        }

}