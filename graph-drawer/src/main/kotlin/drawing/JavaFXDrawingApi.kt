package drawing

import javafx.scene.Group
import javafx.scene.Scene
import javafx.scene.canvas.Canvas
import javafx.scene.paint.Color
import javafx.stage.Stage

class JavaFXDrawingApi(
    private val stage: Stage,
    override val drawingAreaWidth: Int,
    override val drawingAreaHeight: Int
) : DrawingApi {
    private val canvas = Canvas(drawingAreaWidth.toDouble(), drawingAreaHeight.toDouble())
    private val graphicsContext = canvas.graphicsContext2D!!

    override fun show() {
        val root = Group()
        root.children.add(canvas)
        stage.scene = Scene(root, Color.WHITE)
        stage.isResizable = false
        stage.show()
    }

    override fun drawCircle(circle: Circle) {
        graphicsContext.fillOval(
            (circle.center.x - circle.radius).toDouble(),
            (circle.center.y - circle.radius).toDouble(),
            (2 * circle.radius).toDouble(),
            (2 * circle.radius.toDouble())
        )
    }

    override fun drawLine(start: Point, end: Point) {
        graphicsContext.strokeLine(start.x.toDouble(), start.y.toDouble(), end.x.toDouble(), end.y.toDouble())
    }
}