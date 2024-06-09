package io.github.treesitter.ktreesitter

import cnames.structs.TSLanguage
import io.github.treesitter.ktreesitter.internal.*
import kotlinx.cinterop.*

/**
 * A class that defines how to parse a particular language.
 *
 * When a [Language] is generated by the Tree-sitter CLI, it is assigned
 * an ABI [version] number that corresponds to the current CLI version.
 *
 * @constructor Create a new instance from the given language pointer.
 * @param language A [CPointer] to a `TSLanguage`.
 * @throws [IllegalArgumentException] If the pointer is invalid or the [version] is incompatible.
 */
@OptIn(ExperimentalForeignApi::class)
actual class Language @Throws(IllegalArgumentException::class) actual constructor(language: Any) {
    internal val self: CPointer<TSLanguage> =
        (language as? CPointer<*>)?.rawValue?.let(::interpretCPointer)
            ?: throw IllegalArgumentException("Invalid language: $language")

    /** The ABI version number for this language. */
    actual val version: UInt = ts_language_version(self)

    init {
        require(version in MIN_COMPATIBLE_LANGUAGE_VERSION..LANGUAGE_VERSION) {
            "Incompatible language version $version. " +
                "Must be between $MIN_COMPATIBLE_LANGUAGE_VERSION and $LANGUAGE_VERSION."
        }
    }

    /** The number of distinct node types in this language. */
    actual val symbolCount: UInt = ts_language_symbol_count(self)

    /** The number of valid states in this language. */
    actual val stateCount: UInt = ts_language_state_count(self)

    /** The number of distinct field names in this language. */
    actual val fieldCount: UInt = ts_language_field_count(self)

    /** Get the node type for the given numerical ID. */
    actual fun symbolName(symbol: UShort) = ts_language_symbol_name(self, symbol)?.toKString()

    /** Get the numerical ID for the given node type. */
    actual fun symbolForName(name: String, isNamed: Boolean): UShort =
        ts_language_symbol_for_name(self, name, name.length.convert(), isNamed)

    /**
     * Check if the node for the given numerical ID is named
     *
     * @see [Node.isNamed]
     */
    actual fun isNamed(symbol: UShort) =
        ts_language_symbol_type(self, symbol) == TSSymbolTypeRegular

    /** Check if the node for the given numerical ID is visible. */
    actual fun isVisible(symbol: UShort) =
        ts_language_symbol_type(self, symbol) <= TSSymbolTypeAnonymous

    /** Get the field name for the given numerical id. */
    actual fun fieldNameForId(id: UShort) = ts_language_field_name_for_id(self, id)?.toKString()

    /** Get the numerical ID for the given field name. */
    actual fun fieldIdForName(name: String): UShort =
        ts_language_field_id_for_name(self, name, name.length.convert())

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
    actual fun nextState(state: UShort, symbol: UShort): UShort =
        ts_language_next_state(self, state, symbol)

    /**
     * Create a new [lookahead iterator][LookaheadIterator] for the given parse state.
     *
     * @throws [IllegalArgumentException] If the state is invalid for this language.
     */
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

    override fun toString() = "Language(id=${self.rawValue}, version=$version)"
}
