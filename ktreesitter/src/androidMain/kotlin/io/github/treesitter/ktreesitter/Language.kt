package io.github.treesitter.ktreesitter

import dalvik.annotation.optimization.CriticalNative
import dalvik.annotation.optimization.FastNative

/**
 * A class that defines how to parse a particular language.
 *
 * When a [Language] is generated by the Tree-sitter CLI, it is assigned
 * an ABI [version] number that corresponds to the current CLI version.
 *
 * @constructor Create a new instance from the given language pointer.
 * @param language A pointer to a `TSLanguage` cast to [Long].
 * @throws [IllegalArgumentException] If the pointer is invalid or the [version] is incompatible.
 */
actual class Language @Throws(IllegalArgumentException::class) actual constructor(language: Any) {
    @JvmField
    internal val self: Long = (language as? Long)?.takeIf { it > 0L }
        ?: throw IllegalArgumentException("Invalid language: $language")

    init {
        checkVersion()
    }

    /** The ABI version number for this language. */
    @get:JvmName("getVersion")
    actual val version: UInt
        @FastNative external get

    /** The number of distinct node types in this language. */
    @get:JvmName("getSymbolCount")
    actual val symbolCount: UInt
        @FastNative external get

    /** The number of valid states in this language. */
    @get:JvmName("getStateCount")
    actual val stateCount: UInt
        @FastNative external get

    /** The number of distinct field names in this language. */
    @get:JvmName("getFieldCount")
    actual val fieldCount: UInt
        @FastNative external get

    /**
     * Get another reference to the language.
     *
     * @since 0.24.0
     */
    actual fun copy() = Language(copy(self))

    /** Get the node type for the given numerical ID. */
    @FastNative
    @JvmName("symbolName")
    actual external fun symbolName(symbol: UShort): String?

    /** Get the numerical ID for the given node type. */
    @FastNative
    @JvmName("symbolForName")
    actual external fun symbolForName(name: String, isNamed: Boolean): UShort

    /**
     * Check if the node for the given numerical ID is named
     *
     * @see [Node.isNamed]
     */
    @FastNative
    @JvmName("isNamed")
    actual external fun isNamed(symbol: UShort): Boolean

    /** Check if the node for the given numerical ID is visible. */
    @FastNative
    @JvmName("isVisible")
    actual external fun isVisible(symbol: UShort): Boolean

    /**
     * Check if the node for the given numerical ID is a supertype.
     *
     * @since 0.24.0
     */
    @FastNative
    @JvmName("isSupertype")
    actual external fun isSupertype(symbol: UShort): Boolean

    /** Get the field name for the given numerical id. */
    @FastNative
    @JvmName("fieldNameForId")
    actual external fun fieldNameForId(id: UShort): String?

    /** Get the numerical ID for the given field name. */
    @FastNative
    @JvmName("fieldIdForName")
    actual external fun fieldIdForName(name: String): UShort

    /**
     * Get the next parse state.
     *
     * Combine this with [lookaheadIterator] to generate
     * completion suggestions or valid symbols in error nodes.
     *
     * #### Example
     *
     * ```kotlin
     * language.nextState(node.parseState, node.grammarSymbol)
     * ```
     */
    @FastNative
    @JvmName("nextState")
    actual external fun nextState(state: UShort, symbol: UShort): UShort

    /**
     * Create a new [lookahead iterator][LookaheadIterator] for the given parse state.
     *
     * @throws [IllegalArgumentException] If the state is invalid for this language.
     */
    @JvmName("lookaheadIterator")
    @Throws(IllegalArgumentException::class)
    actual fun lookaheadIterator(state: UShort) = LookaheadIterator(this, state)

    /**
     * Create a new [Query] from a string containing one or more S-expression
     * [patterns](https://tree-sitter.github.io/tree-sitter/using-parsers#query-syntax).
     *
     * @throws [QueryError] If any error occurred while creating the query.
     */
    @Throws(QueryError::class)
    actual fun query(source: String) = Query(this, source)

    actual override fun equals(other: Any?) =
        this === other || (other is Language && self == other.self)

    actual override fun hashCode() = self.hashCode()

    override fun toString() = "Language(id=0x${self.toString(16)}, version=$version)"

    @FastNative
    @Throws(IllegalArgumentException::class)
    private external fun checkVersion()

    private companion object {
        @JvmStatic
        @CriticalNative
        private external fun copy(self: Long): Long

        init {
            System.loadLibrary("ktreesitter")
        }
    }
}
