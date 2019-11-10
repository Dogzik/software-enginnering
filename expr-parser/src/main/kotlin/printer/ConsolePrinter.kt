package printer

class ConsolePrinter: Printer {
    override fun write(s: String) {
        print(s)
    }

    override fun writeln() {
        println()
    }
}