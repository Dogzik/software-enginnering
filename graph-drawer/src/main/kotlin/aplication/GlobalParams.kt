package aplication

import drawing.DrawingApi
import graph.Graph

object GlobalParams {
    lateinit var drawer: (DrawingApi) -> Graph
    lateinit var windowParams: WindowParams
}