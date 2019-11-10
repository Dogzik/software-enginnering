package visitor

import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import printer.Printer
import token.*

class PrintVisitorTest {
    private class TestPrinter : Printer {
        val data = StringBuilder()

        override fun write(s: String) {
            data.append(s)
        }

        override fun writeln() {
            data.append("\n")
        }
    }

    private val testPrinter = TestPrinter()
    private val printVisitor = PrintVisitor(testPrinter)

    @Before
    fun cleanPrinter() {
        testPrinter.data.clear()
    }

    private fun getOutput() = testPrinter.data.toString()

    @Test
    fun testEmpty() {
        printVisitor.visit(emptyList())
        assertEquals("\n", getOutput())
    }

    @Test
    fun testSingleToken() {
        val token = NumberToken(2517);
        printVisitor.visit(listOf(token))
        assertEquals(token.toString() + "\n", getOutput())
    }

    @Test
    fun testExpression() {
        val tokens = listOf(
            NumberToken(88),
            MinusToken,
            NumberToken(13),
            PlusToken,
            LeftParenthesisToken,
            NumberToken(2),
            MinusToken,
            NumberToken(0),
            MulToken,
            NumberToken(1),
            DivToken,
            NumberToken(1),
            RightParenthesisToken
        )
        printVisitor.visit(tokens)
        val expected = tokens.joinToString(" ") { it.toString() } + "\n"
        assertEquals(expected, getOutput())
    }
}