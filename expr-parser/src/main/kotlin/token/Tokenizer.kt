package token

class Tokenizer {
    private val tokens = ArrayList<Token>()
    private var curState: State = StartState()

    fun tokenize(s: String): List<Token> {
        curState = StartState()
        s.forEach { curState.process(it) }
        curState.handleEOF()
        require(curState is EOFState)
        return tokens
    }

    private abstract inner class State {
        abstract fun process(c: Char)
        abstract fun handleEOF()
    }

    private inner class EOFState : State() {
        override fun process(c: Char) {
            throw UnsupportedOperationException("No characters could be processed in EOF state")
        }

        override fun handleEOF() {}
    }

    private inner class NumericState : State() {
        private var number = 0;

        override fun process(c: Char) {
            when (c) {
                in '0'..'9' -> {
                    number = number * 10 + (c - '0')
                }
                else -> {
                    this@Tokenizer.tokens.add(NumberToken(number))
                    this@Tokenizer.curState = StartState()
                    this@Tokenizer.curState.process(c)
                }
            }
        }

        override fun handleEOF() {
            this@Tokenizer.tokens.add(NumberToken(number))
            this@Tokenizer.curState = EOFState()
        }
    }

    private inner class StartState : State() {
        override fun process(c: Char) {
            when (c) {
                '(' -> this@Tokenizer.tokens.add(LeftParenthesisToken)
                ')' -> this@Tokenizer.tokens.add(RightParenthesisToken)
                '+' -> this@Tokenizer.tokens.add(PlusToken)
                '-' -> this@Tokenizer.tokens.add(MinusToken)
                '*' -> this@Tokenizer.tokens.add(MulToken)
                '/' -> this@Tokenizer.tokens.add(DivToken)
                in '0'..'9' -> {
                    this@Tokenizer.curState = NumericState()
                    this@Tokenizer.curState.process(c)
                }
                else -> {
                    require(c.isWhitespace()) { "Unexpected character $c in the input" }
                }
            }
        }

        override fun handleEOF() {
            this@Tokenizer.curState = EOFState()
        }
    }
}