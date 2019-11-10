package visitor

import token.*
import java.util.*
import kotlin.properties.Delegates

class CalcVisitor : TokenVisitor {
    private companion object {
        private val operations: Map<ArithmeticOperationToken, (Int, Int) -> Int> = mapOf(
            Pair(PlusToken, Int::plus),
            Pair(MinusToken, Int::minus),
            Pair(MulToken, Int::minus),
            Pair(DivToken, Int::div)
        )
    }

    private val stack = Stack<Int>()
    private var result by Delegates.notNull<Int>()

    private fun doVisit(token: ParenthesisToken) {
        throw IllegalArgumentException("Parenthesis are not allowed on RPN")
    }

    private fun doVisit(token: NumberToken) {
        stack.add(token.n)
    }

    private fun doVisit(token: ArithmeticOperationToken) {
        check(stack.size >= 2) { "Two arguments are required for operation" }
        val x = stack.pop()
        val y = stack.pop()
        val op = operations[token]
        if (op == null) {
            throw IllegalArgumentException("Unrecognized operation")
        } else {
            stack.push(op(y, x))
        }
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
        check(stack.size == 1) { "RPN expression have more than 1 element on stack after evaluation" }
        result = stack.pop()
    }

    fun fetchResult(): Int = result
}