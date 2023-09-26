import enums.AtomIdentifier
import utils.ByteStreamReader

abstract class Atom(
    var identifier: String,
    var size: Int
) {

//    var key: AtomKey?
//        get() = when {
//            StringMetadataIdentifier.valueOf(identifier) != null -> AtomKey(idString = identifier)
//            IntegerMetadataIdentifier.valueOf(identifier) != null -> AtomKey(idString = identifier)
//            identifier in listOf("trkn", "disk", "covr", "----") -> AtomKey(idString = identifier)
//            else -> null
//        }
//        set(value) {}

//    override fun toString(): String {
//        return key?.let { throw IllegalStateException("Override from metadata atom $identifier") } ?: identifier
//    }

    abstract suspend fun parse(): Atom


    private var _children = mutableListOf<Atom>()
    var children: List<Atom>
        get() = _children
        set(value) {
            _children = value.toMutableList()
            for (child in _children) {
                child.parent = this
            }
        }

    var parent: Atom? = null
    var siblings: List<Atom>?
        get() = parent?.children
        set(value) {
            value?.let {
                parent?.children = it
            }
        }

    constructor(identifier: String, size: Int, payload: ByteStreamReader) : this(identifier, size) {
        // You can add payload parsing logic here if necessary
        this.identifier = identifier
        this.size = size
        _children = mutableListOf()
    }

    constructor(identifier: String, size: Int, children: List<Atom>) : this(identifier, size) {
        this._children = children.toMutableList()
        for (child in _children) {
            child.parent = this
        }
    }

    constructor(identifier: String, size: Int, payload: ByteStreamReader, children: List<Atom>) : this(identifier, size) {
        this._children = children.toMutableList()
        for (child in _children) {
            child.parent = this
        }
        // You can add payload parsing logic here if necessary
    }

    operator fun get(identifier: AtomIdentifier): Atom? {
        return children.firstOrNull { it.identifier == identifier.rawValue }
    }

    operator fun set(identifier: AtomIdentifier, newValue: Atom?) {
        children = if (newValue != null) {
            children.filter { it.identifier != identifier.rawValue } + newValue
        } else {
            children.filter { it.identifier != identifier.rawValue }
        }
    }

    // Placeholder for a function to override in subclasses
    open val contentData: ByteArray
        get() = throw IllegalStateException("Override contentData in subclass: ${this::class.simpleName}")

    companion object {
        val version: ByteArray
            get() = byteArrayOf(0x00)

        val flags: ByteArray
            get() = ByteArray(3) { 0x00 }
    }
}