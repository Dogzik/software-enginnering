package printer

interface Printer {
    fun write(s: String)
    fun writeln()
    fun writeln(s: String) {
        print(s)
        println()
    }
}