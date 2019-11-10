package visitor

import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import token.*

class RPNConverterVisitorTest {
    private var visitor = RPNConverterVisitor()

    @Before
    fun prepare() {
        visitor = RPNConverterVisitor()
    }

    @Test
    fun testSimple() {
        val a = NumberToken(1)
        val b = NumberToken(2)
        val op = PlusToken
        visitor.visit(listOf(a, op, b))
        assertEquals(listOf(a, b, op), visitor.getConvertedExpression())
    }

    @Test
    fun testSimpleParenthesis() {
        val a = NumberToken(1)
        val b = NumberToken(2)
        val c = NumberToken(3)
        val tokens = listOf(LeftParenthesisToken, a, PlusToken, b, RightParenthesisToken, MulToken, c)
        visitor.visit(tokens)
        assertEquals(listOf(a, b, PlusToken, c, MulToken), visitor.getConvertedExpression())
    }

    @Test
    fun testDifferentPriorities() {
        val a = NumberToken(1)
        val b = NumberToken(2)
        val c = NumberToken(3)
        val d = NumberToken(4)
        val e = NumberToken(5)
        val tokens = listOf(
            a,
            PlusToken,
            b,
            MulToken,
            c,
            DivToken,
            d,
            PlusToken,
            e
        )
        visitor.visit(tokens)
        assertEquals(
            listOf(
                a,
                b,
                c,
                MulToken,
                d,
                DivToken,
                PlusToken,
                e,
                PlusToken
            ),
            visitor.getConvertedExpression()
        )
    }

    @Test(expected = IllegalStateException::class)
    fun noMatchingBracketTest() {
        val tokens = listOf(
            LeftParenthesisToken,
            NumberToken(2),
            PlusToken,
            NumberToken(3)
        )
        visitor.visit(tokens)
    }
}