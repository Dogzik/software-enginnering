package aplication

import drawing.JavaFXDrawingApi
import javafx.application.Application
import javafx.stage.Stage

class JavaFXApplication :
    Application(),
    DrawingApplication {

    override fun startApplication() {
        launch(this::class.java)
    }

    override fun start(primaryStage: Stage) {
        val drawingApi = JavaFXDrawingApi(
            primaryStage,
            GlobalParams.windowParams.width,
            GlobalParams.windowParams.height
        )
        GlobalParams.drawer(drawingApi).drawGraph()
    }
}