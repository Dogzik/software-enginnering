package graph

import drawing.Circle
import drawing.DrawingApi
import drawing.Point
import kotlin.math.*

abstract class Graph(protected val drawingApi: DrawingApi, protected open val totalVertices: Int) {
    abstract fun drawGraph()

    protected open val center = Point(drawingApi.drawingAreaWidth / 2, drawingApi.drawingAreaHeight / 2)
    protected open val plotRadius = min(drawingApi.drawingAreaHeight, drawingApi.drawingAreaWidth) * 0.3
    protected open val vertexRadius =
        sqrt(min(drawingApi.drawingAreaHeight, drawingApi.drawingAreaHeight).toDouble()).toInt() / 2

    protected open fun getVertexCenter(idx: Int) = Point(
        (center.x + plotRadius * cos(PI * 2 * idx / totalVertices)).toInt(),
        (center.y + plotRadius * sin(PI * 2 * idx / totalVertices)).toInt()
    )

    protected open fun drawVertices() {
        for (idx in 0 until totalVertices) {
            drawingApi.drawCircle(Circle(getVertexCenter(idx), vertexRadius))
        }
    }

    protected open fun drawEdge(fromIdx: Int, toIdx: Int) {
        drawingApi.drawLine(getVertexCenter(fromIdx), getVertexCenter(toIdx))
    }
}