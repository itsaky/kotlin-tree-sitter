package io.github.treesitter.ktreesitter

import br.com.colman.kotest.KotestRunnerAndroid
import io.github.treesitter.ktreesitter.java.TreeSitterJava
import io.kotest.assertions.throwables.shouldNotThrowAny
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.*
import io.kotest.matchers.comparables.*
import io.kotest.matchers.nulls.*
import io.kotest.matchers.string.*
import io.kotest.matchers.types.*
import org.junit.runner.RunWith

@RunWith(KotestRunnerAndroid::class)
class LanguageTest : FunSpec({
    val language = Language(TreeSitterJava.language())

    test("version") {
        language.version shouldBe 14U
    }

    test("symbolCount") {
        language.symbolCount shouldBeGreaterThan 1U
    }

    test("stateCount") {
        language.stateCount shouldBeGreaterThan 1U
    }

    test("fieldCount") {
        language.fieldCount shouldBeGreaterThan 1U
    }

    test("symbolName()") {
        language.symbolName(1U) shouldBe "identifier"
    }

    test("symbolForName()") {
        language.symbolForName(";", false) shouldBeGreaterThan 0U
        language.symbolForName("program", true) shouldBeGreaterThan 0U
    }

    test("isNamed()") {
        language.isNamed(1U) shouldBe true
    }

    test("isVisible()") {
        language.isVisible(1U) shouldBe true
    }

    test("isSupertype()") {
        language.isSupertype(1U) shouldBe false
    }

    test("fieldNameForId()") {
        language.fieldNameForId(1U).shouldNotBeNull()
    }

    test("fieldIdForName()") {
        language.fieldIdForName("body") shouldBeGreaterThan 0U
    }

    test("nextState()") {
        val program = language.symbolForName("program", true)
        language.nextState(1U, program) shouldBeGreaterThan 0U
    }

    test("lookaheadIterator()") {
        val program = language.symbolForName("program", true)
        val state = language.nextState(1U, program)
        val lookahead = language.lookaheadIterator(state)
        lookahead.language shouldBe language
    }

    test("query()") {
        shouldNotThrowAny { language.query("(program) @root") }
    }

    test("equals()") {
        Language(TreeSitterJava.language()) shouldBe language.copy()
    }

    test("hashCode()") {
        language shouldHaveSameHashCodeAs TreeSitterJava.language()
    }

    test("toString()") {
        language.toString() shouldMatch Regex("""Language\(id=0x[0-9a-f]+, version=14\)""")
    }
})
