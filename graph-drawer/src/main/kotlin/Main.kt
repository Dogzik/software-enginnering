import aplication.*
import graph.ListGraph
import graph.MatrixGraph

fun main(args: Array<String>) {
    GlobalParams.windowParams = WindowParams(600, 400)
    try {
        require(args.size == 2) { "Usage: <drawing api> <graph type>" }
        val type = when (args[1]) {
            "matrix" -> GraphType.MATRIX
            "list" -> GraphType.LIST
            else -> throw IllegalArgumentException("Graph type not specified, should be 'matrix' or 'list'")
        }
        GlobalParams.drawer = when (type) {
            GraphType.LIST -> ListGraph.readGraph()
            GraphType.MATRIX -> MatrixGraph.readGraph()
        }
        val application = when (args[0]) {
            "javaFX" -> JavaFXApplication()
            "awt" -> AWTApplication()
            else -> throw IllegalArgumentException("Drawing API not specified, should be 'javaFX' or 'awt'")
        }
        application.startApplication()
    } catch (e: IllegalArgumentException) {
        println("Incorrect usage of program: ${e.message}")
    }
}