import printer.ConsolePrinter
import token.Tokenizer
import visitor.CalcVisitor
import visitor.PrintVisitor
import visitor.RPNConverterVisitor

fun main() {
    val input = readLine() ?: throw IllegalArgumentException("Input is null")
    try {
        val tokens = Tokenizer().tokenize(input)
        val printVisitor = PrintVisitor(ConsolePrinter())
        println("Expression after tokenization:")
        printVisitor.visit(tokens)

        val parseVisitor = RPNConverterVisitor()
        parseVisitor.visit(tokens)
        val RPNTokens = parseVisitor.getConvertedExpression()
        println("Expression after transformation to RPN:")
        printVisitor.visit(RPNTokens)

        val calcVisitor = CalcVisitor()
        calcVisitor.visit(RPNTokens)
        println("Result of evaluation: ${calcVisitor.fetchResult()}")
    } catch (e: Throwable) {
        println("Error occurred during execution: ${e.message}")
    }
}