package visitor

import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import token.*

class CalcVisitorTest {
    private var calcVisitor = CalcVisitor()

    @Before
    fun prepareVisitor() {
        calcVisitor = CalcVisitor()
    }

    @Test
    fun testVerySimpleExpression() {
        val num = 22;
        val tokens = listOf(NumberToken(num))
        calcVisitor.visit(tokens)
        assertEquals(num, calcVisitor.fetchResult())
    }

    @Test(expected = IllegalStateException::class)
    fun testNonVisiting() {
        calcVisitor.fetchResult()
    }

    @Test
    fun testSimpleExpression() {
        val a = 2
        val b = 7
        val tokens = listOf(NumberToken(a), NumberToken(b), PlusToken)
        calcVisitor.visit(tokens)
        assertEquals(a + b, calcVisitor.fetchResult())
    }

    @Test
    fun testComplexExpression() {
        val tokens = listOf(
            NumberToken(2),
            NumberToken(3),
            NumberToken(5),
            PlusToken,
            MinusToken,
            NumberToken(2),
            DivToken
        )
        calcVisitor.visit(tokens)
        assertEquals(calcVisitor.fetchResult(), -3)
    }

    @Test(expected = IllegalStateException::class)
    fun testMissingOperand() {
        val tokens = listOf(NumberToken(1), MulToken)
        calcVisitor.visit(tokens)
    }
}