package visitor

import token.Token

interface TokenVisitor {
    fun visit(token: Token)
    fun visit(tokens: List<Token>)
}