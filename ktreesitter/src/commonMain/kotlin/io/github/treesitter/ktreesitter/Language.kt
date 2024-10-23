package io.github.treesitter.ktreesitter

/**
 * A class that defines how to parse a particular language.
 *
 * When a [Language] is generated by the Tree-sitter CLI, it is assigned
 * an ABI [version] number that corresponds to the current CLI version.
 *
 * @constructor Create a new instance from the given language pointer.
 * @throws [IllegalArgumentException] If the pointer is invalid or the [version] is incompatible.
 */
expect class Language @Throws(IllegalArgumentException::class) constructor(language: Any) {
    /** The ABI version number for this language. */
    val version: UInt

    /** The number of distinct node types in this language. */
    val symbolCount: UInt

    /** The number of valid states in this language. */
    val stateCount: UInt

    /** The number of distinct field names in this language. */
    val fieldCount: UInt

    /** Get the node type for the given numerical ID. */
    fun symbolName(symbol: UShort): String?

    /** Get the numerical ID for the given node type. */
    fun symbolForName(name: String, isNamed: Boolean): UShort

    /**
     * Check if the node for the given numerical ID is named
     *
     * @see [Node.isNamed]
     */
    fun isNamed(symbol: UShort): Boolean

    /** Check if the node for the given numerical ID is visible. */
    fun isVisible(symbol: UShort): Boolean

    /**
     * Check if the node for the given numerical ID is a supertype.
     *
     * @since 0.24.0
     */
    fun isSupertype(symbol: UShort): Boolean

    /** Get the field name for the given numerical id. */
    fun fieldNameForId(id: UShort): String?

    /** Get the numerical ID for the given field name. */
    fun fieldIdForName(name: String): UShort

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
    fun nextState(state: UShort, symbol: UShort): UShort

    /**
     * Create a new [lookahead iterator][LookaheadIterator] for the given parse state.
     *
     * @throws [IllegalArgumentException] If the state is invalid for this language.
     */
    @Throws(IllegalArgumentException::class)
    fun lookaheadIterator(state: UShort): LookaheadIterator

    /**
     * Create a new [Query] from a string containing one or more S-expression
     * [patterns](https://tree-sitter.github.io/tree-sitter/using-parsers#query-syntax).
     *
     * @throws [QueryError] If any error occurred while creating the query.
     */
    @Throws(QueryError::class)
    fun query(source: String): Query

    override fun equals(other: Any?): Boolean

    override fun hashCode(): Int
}
