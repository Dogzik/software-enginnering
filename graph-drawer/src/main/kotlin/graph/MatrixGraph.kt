package graph

import drawing.Circle
import drawing.DrawingApi
import drawing.Point
import kotlin.math.*

class MatrixGraph(private val matrix: Array<BooleanArray>, drawingApi: DrawingApi) : Graph(drawingApi, matrix.size) {
    override fun drawGraph() {
        drawVertices()
        for (i in 0 until totalVertices) {
            for (j in 0 until totalVertices) {
                if (matrix[i][j]) {
                    drawEdge(i, j)
                }
            }
        }
        drawingApi.show()
    }

    companion object {
        fun readGraph(): (DrawingApi) -> Graph {
            val n = readLine()?.toInt() ?: throw IllegalArgumentException("Number of vertices should be specified")
            val result = Array(n) {
                BooleanArray(0) {
                    false
                }
            }
            for (i in 0 until n) {
                val edges = readLine()?.split(" ")?.map {
                    it == "1"
                }?.toBooleanArray() ?: throw IllegalArgumentException("${i + 1} line of matrix should be specified")
                require(edges.size == n) { "Every line must contain $n numbers" }
                result[i] = edges
            }
            return { api -> MatrixGraph(result, api) }
        }
    }
}