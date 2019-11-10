package visitor

import token.*
import java.util.*
import kotlin.collections.ArrayList

class RPNConverterVisitor : TokenVisitor {
    private companion object {
        private val opPriority: Map<ArithmeticOperationToken, Int> = mapOf(
            Pair(PlusToken, 0),
            Pair(MinusToken, 0),
            Pair(DivToken, 1),
            Pair(MulToken, 1)
        )
    }

    private val stack = Stack<Token>()
    private val result = ArrayList<Token>()

    fun getConvertedExpression(): List<Token> = result

    private fun doVisit(token: NumberToken) {
        result.add(token)
    }

    private fun doVisit(token: ParenthesisToken) {
        when (token) {
            is LeftParenthesisToken -> stack.push(token)
            is RightParenthesisToken -> {
                loop@ while (!stack.empty()) {
                    val lastToken = stack.peek()
                    when (lastToken) {
                        is LeftParenthesisToken -> {
                            stack.pop()
                            break@loop
                        }
                        is ArithmeticOperationToken -> {
                            result.add(lastToken)
                            stack.pop()
                        }
                        is RightParenthesisToken, is NumberToken ->
                            throw IllegalStateException("Wrong stack state: ${stack.toList()}")
                    }
                }
            }
        }
    }

    private fun doVisit(token: ArithmeticOperationToken) {
        while (!stack.empty()) {
            val lastToken = stack.peek()
            if (lastToken is ArithmeticOperationToken) {
                val curTokenPriority = opPriority[token]
                val lastTokePriority = opPriority[lastToken]
                if (curTokenPriority == null || lastTokePriority == null) {
                    throw IllegalArgumentException("Some operations are unrecognized: $token $lastToken")
                }
                if (curTokenPriority <= lastTokePriority) {
                    result.add(lastToken)
                    stack.pop()
                } else {
                    break
                }
            } else {
                break
            }
        }
        stack.push(token)
    }

    override fun visit(token: Token) {
        when (token) {
            is ArithmeticOperationToken -> doVisit(token)
            is NumberToken -> doVisit(token)
            is ParenthesisToken -> doVisit(token)
        }
    }

    override fun visit(tokens: List<Token>) {
        tokens.forEach { it.accept(this) }
        while (!stack.empty()) {
            val lastToken = stack.peek()
            if (lastToken is ArithmeticOperationToken) {
                result.add(lastToken)
                stack.pop()
            } else {
                throw IllegalStateException(
                    "Only operations allowed in the end of transformation\n" +
                            "No matching closing bracket\n" +
                            "Stack is ${stack.toList()}"
                )
            }
        }
    }
}