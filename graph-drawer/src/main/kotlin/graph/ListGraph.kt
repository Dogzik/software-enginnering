package graph

import drawing.Circle
import drawing.DrawingApi
import drawing.Point
import kotlin.math.*

data class Edge(val from: Int, val to: Int)

class ListGraph(totalVertices: Int, private val edges: List<Edge>, drawingApi: DrawingApi) :
    Graph(drawingApi, totalVertices) {
    override fun drawGraph() {
        drawVertices()
        for (edge in edges) {
            drawEdge(edge.from, edge.to)
        }
        drawingApi.show()
    }

    companion object {
        fun readGraph(): (DrawingApi) -> Graph {
            val n = readLine()?.toInt() ?: throw IllegalArgumentException("Specify number of vertices")
            val m = readLine()?.toInt() ?: throw IllegalArgumentException("Specify number of edges")
            val edges = List(m) {
                val line = readLine()
                    ?.split(" ")
                    ?.map { it.toInt() }
                    ?: throw IllegalArgumentException("Specify ends of edge")
                require(line.size == 2) { "Specify exactly two ends" }
                val from = line[0]
                val to = line[1]
                Edge(from, to)
            }
            return { api -> ListGraph(n, edges, api) }
        }
    }
}