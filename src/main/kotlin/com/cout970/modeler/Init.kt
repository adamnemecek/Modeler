package com.cout970.modeler

import com.cout970.glutilities.event.EventManager
import com.cout970.glutilities.window.GLFWLoader
import com.cout970.modeler.event.EventController

/**
 * Created by cout970 on 2016/11/29.
 */
class Init {

    lateinit var windowController: WindowController
    lateinit var resourceManager: ResourceManager
    lateinit var modelController: ModelController
    lateinit var eventController: EventController
    lateinit var renderManager: RenderManager
    lateinit var mainController: MainController


    fun run() {
        start()
        mainController.loop()
        stop()
    }

    fun start() {
        windowController = WindowController()
        resourceManager = ResourceManager()
        modelController = ModelController()
        eventController = EventController()
        renderManager = RenderManager()
        mainController = MainController(listOf(eventController, renderManager, modelController, windowController))
        windowController.stop = { mainController.stop = true}

        windowController.registerListeners(eventController)

        GLFWLoader.init()
        windowController.show()
        EventManager.registerWindow(windowController.window.id)
        renderManager.initOpenGl()
    }

    fun stop() {
        GLFWLoader.terminate()
    }
}