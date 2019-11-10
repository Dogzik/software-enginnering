package token

import org.junit.Test
import org.junit.Assert.*
import org.junit.Before

class TokenizerTest {
    private var tokenizer = Tokenizer();

    @Before
    fun prepareTokenizer() {
        tokenizer = Tokenizer()
    }

    @Test
    fun testSimpleExpression() {
        val s = "2 + 2"
        val tokens = tokenizer.tokenize(s)
        assertEquals(listOf(NumberToken(2), PlusToken, NumberToken(2)), tokens)
    }

    @Test
    fun testSimpleExpressionWithoutWhiteSpaces() {
        val s = "31+7*4"
        val tokens = tokenizer.tokenize(s)
        assertEquals(
            listOf(
                NumberToken(31),
                PlusToken,
                NumberToken(7),
                MulToken,
                NumberToken(4)
            ),
            tokens
        )
    }

    @Test
    fun testParenthesis() {
        val s = "9 / (4- 1)"
        val tokens = tokenizer.tokenize(s)
        assertEquals(
            listOf(
                NumberToken(9),
                DivToken,
                LeftParenthesisToken,
                NumberToken(4),
                MinusToken,
                NumberToken(1),
                RightParenthesisToken
            ),
            tokens
        )
    }

    @Test(expected = IllegalArgumentException::class)
    fun incorrectTest() {
        val s = "1 / 2 + j"
        tokenizer.tokenize(s)
    }
}