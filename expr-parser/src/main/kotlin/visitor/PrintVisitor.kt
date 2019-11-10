package visitor

import printer.Printer
import token.Token

class PrintVisitor(private val printer: Printer) : TokenVisitor {
    override fun visit(token: Token) {
        printer.write(token.toString())
    }

    override fun visit(tokens: List<Token>) {
        tokens.forEachIndexed { idx, token ->
            token.accept(this)
            if (idx != tokens.size - 1) {
                printer.write(" ")
            }
        }
        printer.writeln()
    }
}