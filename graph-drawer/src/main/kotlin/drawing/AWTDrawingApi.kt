package drawing

import java.awt.Graphics2D
import java.awt.geom.Ellipse2D

class AWTDrawingApi(
    private val graphics2D: Graphics2D,
    override val drawingAreaWidth: Int,
    override val drawingAreaHeight: Int
) : DrawingApi {
    override fun drawCircle(circle: Circle) {
        graphics2D.fill(
            Ellipse2D.Double(
                (circle.center.x - circle.radius).toDouble(),
                (circle.center.y - circle.radius).toDouble(),
                (circle.radius * 2).toDouble(),
                (circle.radius * 2).toDouble()
            )
        )
    }

    override fun drawLine(start: Point, end: Point) {
        graphics2D.drawLine(start.x, start.y, end.x, end.y)
    }

    override fun show() {}
}