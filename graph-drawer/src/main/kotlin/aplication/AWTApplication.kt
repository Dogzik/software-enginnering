package aplication

import drawing.AWTDrawingApi
import java.awt.Frame
import java.awt.Graphics
import java.awt.Graphics2D
import java.awt.event.WindowAdapter
import java.awt.event.WindowEvent
import kotlin.system.exitProcess

class AWTApplication() : Frame(),
    DrawingApplication {
    override fun startApplication() {
        addWindowListener(
            object : WindowAdapter() {
                override fun windowClosing(e: WindowEvent?) {
                    exitProcess(0)
                }
            }
        )
        setSize(GlobalParams.windowParams.width, GlobalParams.windowParams.height)
        isVisible = true
    }

    override fun paint(graphics: Graphics) {
        super.paint(graphics)
        val graphics2D = graphics as Graphics2D
        graphics2D.clearRect(0, 0, GlobalParams.windowParams.width, GlobalParams.windowParams.height)
        val drawingApi = AWTDrawingApi(
            graphics2D,
            GlobalParams.windowParams.width,
            GlobalParams.windowParams.height
        )
        GlobalParams.drawer(drawingApi).drawGraph()
        isResizable = false
    }
}