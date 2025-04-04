package io.github.treesitter.ktreesitter

import dalvik.annotation.optimization.FastNative

/** A single node within a [syntax tree][Tree]. */
@Suppress("unused")
actual class Node internal constructor(
    id: Long,
    private var context: IntArray,
    @JvmField internal val tree: Tree
) {
    /**
     * The numeric ID of the node.
     *
     * Within any given syntax tree, no two nodes have the same ID.
     * However, if a new tree is created based on an older tree,
     * and a node from the old tree is reused in the process,
     * then that node will have the same ID in both trees.
     */
    @get:JvmName("getId")
    actual val id: ULong = id.toULong()

    /** The numerical ID of the node's type. */
    @get:JvmName("getSymbol")
    actual val symbol: UShort
        @FastNative external get

    /**
     * The numerical ID of the node's type,
     * as it appears in the grammar ignoring aliases.
     */
    @get:JvmName("getGrammarSymbol")
    actual val grammarSymbol: UShort
        @FastNative external get

    /** The type of the node. */
    actual val type: String
        @FastNative external get

    /**
     * The type of the node,
     * as it appears in the grammar ignoring aliases.
     */
    actual val grammarType: String
        @FastNative external get

    /**
     * Check if the node is _named_.
     *
     * Named nodes correspond to named rules in the grammar,
     * whereas _anonymous_ nodes correspond to string literals.
     */
    actual val isNamed: Boolean
        @FastNative external get

    /**
     * Check if the node is _extra_.
     *
     * Extra nodes represent things which are not required
     * by the grammar but can appear anywhere (e.g. whitespace).
     */
    actual val isExtra: Boolean
        @FastNative external get

    /** Check if the node is a syntax error. */
    actual val isError: Boolean
        @FastNative external get

    /**
     * Check if the node is _missing_.
     *
     * Missing nodes are inserted by the parser in order
     * to recover from certain kinds of syntax errors.
     */
    actual val isMissing: Boolean
        @FastNative external get

    /** Check if the node has been edited. */
    @get:JvmName("hasChanges")
    actual val hasChanges: Boolean
        @FastNative external get

    /**
     * Check if the node is a syntax error,
     * or contains any syntax errors.
     */
    @get:JvmName("hasError")
    actual val hasError: Boolean
        @FastNative external get

    /** The parse state of this node. */
    @get:JvmName("getParseState")
    actual val parseState: UShort
        @FastNative external get

    /** The parse state after this node. */
    @get:JvmName("getNextParseState")
    actual val nextParseState: UShort
        @FastNative external get

    /** The start byte of the node. */
    @get:JvmName("getStartByte")
    actual val startByte: UInt
        @FastNative external get

    /** The end byte of the node. */
    @get:JvmName("getEndByte")
    actual val endByte: UInt
        @FastNative external get

    /** The range of the node in terms of bytes. */
    actual val byteRange: UIntRange
        get() = startByte..endByte

    /** The range of the node in terms of bytes and points. */
    actual val range: Range
        get() = Range(startPoint, endPoint, startByte, endByte)

    /** The start point of the node. */
    actual val startPoint: Point
        @FastNative external get

    /** The end point of the node. */
    actual val endPoint: Point
        @FastNative external get

    /** The number of this node's children. */
    @get:JvmName("getChildCount")
    actual val childCount: UInt
        @FastNative external get

    /** The number of this node's _named_ children. */
    @get:JvmName("getNamedChildCount")
    actual val namedChildCount: UInt
        @FastNative external get

    /**
     * The number of this node's descendants,
     * including one for the node itself.
     */
    @get:JvmName("getDescendantCount")
    actual val descendantCount: UInt
        @FastNative external get

    /** The node's immediate parent, if any. */
    actual val parent: Node?
        @FastNative external get

    /** The node's next sibling, if any. */
    actual val nextSibling: Node?
        @FastNative external get

    /** The node's previous sibling, if any. */
    actual val prevSibling: Node?
        @FastNative external get

    /** The node's next _named_ sibling, if any. */
    actual val nextNamedSibling: Node?
        @FastNative external get

    /** The node's previous _named_ sibling, if any. */
    actual val prevNamedSibling: Node?
        @FastNative external get

    @Suppress("unused")
    private var internalChildren: List<Node>? = null

    /**
     * This node's children.
     *
     * If you're walking the tree recursively,
     * you may want to use [walk] instead.
     */
    actual val children: List<Node>
        external get

    /** This node's _named_ children. */
    actual val namedChildren: List<Node>
        get() = children.filter(Node::isNamed)

    /**
     * The node's child at the given index, if any.
     *
     * This method is fairly fast, but its cost is technically
     * `log(i)`, so if you might be iterating over a long list of
     * children, you should use [children] or [Tree.walk] instead.
     *
     * @throws [IndexOutOfBoundsException]
     *  If the index exceeds the [child count][childCount].
     */
    @JvmName("child")
    @Throws(IndexOutOfBoundsException::class)
    actual external fun child(index: UInt): Node?

    /**
     * Get the node's _named_ child at the given index, if any.
     *
     * This method is fairly fast, but its cost is technically `log(i)`,
     * so if you might be iterating over a long list of children,
     * you should use [namedChildren] or [walk][Node.walk] instead.
     *
     * @throws [IndexOutOfBoundsException]
     *  If the index exceeds the [named child count][namedChildCount].
     */
    @FastNative
    @JvmName("namedChild")
    @Throws(IndexOutOfBoundsException::class)
    actual external fun namedChild(index: UInt): Node?

    /**
     * Get the node's child with the given field ID, if any.
     *
     * @see [Language.fieldIdForName]
     */
    @FastNative
    @JvmName("childByFieldId")
    actual external fun childByFieldId(id: UShort): Node?

    /** Get the node's child with the given field name, if any. */
    actual external fun childByFieldName(name: String): Node?

    /** Get a list of children with the given field ID. */
    @FastNative
    @JvmName("childrenByFieldId")
    actual external fun childrenByFieldId(id: UShort): List<Node>

    /** Get a list of children with the given field name. */
    actual fun childrenByFieldName(name: String) =
        childrenByFieldId(tree.language.fieldIdForName(name))

    /**
     * Get the field name of this node’s child at the given index, if available.
     *
     * @throws [IndexOutOfBoundsException] If the index exceeds the [child count][childCount].
     */
    @FastNative
    @JvmName("fieldNameForChild")
    @Throws(IndexOutOfBoundsException::class)
    actual external fun fieldNameForChild(index: UInt): String?

    /**
     * Get the field name of this node’s _named_ child at the given index, if available.
     *
     * @throws [IndexOutOfBoundsException] If the index exceeds the [child count][childCount].
     * @since 0.24.0
     */
    @FastNative
    @JvmName("fieldNameForNamedChild")
    @Throws(IndexOutOfBoundsException::class)
    actual external fun fieldNameForNamedChild(index: UInt): String?

    /** Get the child of the node that contains the given descendant, if any. */
    @FastNative
    @Deprecated(
        "This method will not return a direct descendant",
        ReplaceWith("childWithDescendant(descendant)", "io.github.treesitter.ktreesitter.Node")
    )
    actual external fun childContainingDescendant(descendant: Node): Node?

    /**
     * Get the node that contains the given descendant, if any.
     *
     * @since 0.24.0
     */
    @FastNative
    actual external fun childWithDescendant(descendant: Node): Node?

    /**
     * Get the smallest node within this node
     * that spans the given byte range, if any.
     */
    @FastNative
    @JvmName("descendant")
    actual external fun descendant(start: UInt, end: UInt): Node?

    /**
     * Get the smallest node within this node
     * that spans the given point range, if any.
     */
    @FastNative
    actual external fun descendant(start: Point, end: Point): Node?

    /**
     * Get the smallest _named_ node within this node
     * that spans the given byte range, if any.
     */
    @FastNative
    @JvmName("namedDescendant")
    actual external fun namedDescendant(start: UInt, end: UInt): Node?

    /**
     * Get the smallest _named_ node within this node
     * that spans the given point range, if any.
     */
    @FastNative
    actual external fun namedDescendant(start: Point, end: Point): Node?

    /**
     * Edit this node to keep it in-sync with source code that has been edited.
     *
     * This method is only rarely needed. When you edit a syntax tree via
     * [Tree.edit], all the nodes that you retrieve from the tree afterward
     * will already reflect the edit. You only need to use this when you have a
     * specific Node instance that you want to keep and continue to use after an edit.
     */
    @FastNative
    actual external fun edit(edit: InputEdit)

    /** Create a new tree cursor starting from this node. */
    actual fun walk() = TreeCursor(this)

    /** Get the source code of the node, if available. */
    actual fun text() = tree.text()?.run {
        subSequence(startByte.toInt(), minOf(endByte.toInt(), length))
    }

    /** Get the S-expression of the node. */
    actual external fun sexp(): String

    actual override fun equals(other: Any?) =
        this === other || (other is Node && nativeEquals(other))

    @FastNative
    actual external override fun hashCode(): Int

    override fun toString() = "Node(type=$type, startByte=$startByte, endByte=$endByte)"

    @FastNative
    private external fun nativeEquals(that: Node): Boolean
}
