package com.cout970.modeler.view

import com.cout970.modeler.event.EventHandler
import com.cout970.modeler.project.ProjectManager
import com.cout970.modeler.util.ITickeable
import com.cout970.modeler.view.controller.ButtonController
import com.cout970.modeler.view.controller.ModuleController
import com.cout970.modeler.view.controller.SceneController
import com.cout970.modeler.view.gui.RootFrame
import com.cout970.modeler.view.gui.TextureHandler
import com.cout970.modeler.view.scene.SceneModel
import com.cout970.modeler.view.scene.SceneTexture
import com.cout970.modeler.window.WindowHandler

/**
 * Created by cout970 on 2016/12/27.
 */

class UIManager(
        val windowHandler: WindowHandler,
        eventHandler: EventHandler,
        renderManager: RenderManager,
        textureHandler: TextureHandler,
        private val projectManager: ProjectManager) : ITickeable {

    val sceneController: SceneController
    val moduleController: ModuleController
    val buttonController: ButtonController

    val rootFrame: RootFrame

    init {
        renderManager.uiManager = this
        buttonController = ButtonController(projectManager, this)
        rootFrame = RootFrame(eventHandler, windowHandler, buttonController)
        sceneController = SceneController(projectManager.modelEditor, eventHandler, rootFrame, windowHandler.timer)
        moduleController = ModuleController(projectManager.modelEditor, rootFrame, buttonController, eventHandler,
                textureHandler)
        addScenes()
    }

    fun addScenes() {
        sceneController.scenes += SceneModel(projectManager.modelEditor, windowHandler, sceneController)
//        sceneController.scenes += ModelScene(sceneController).apply { perspective = false; camera = camera.copy(angleX = 0.0, angleY = 0.0)  }
//        sceneController.scenes += ModelScene(sceneController).apply { perspective = false; camera = camera.copy(angleX = 0.0, angleY = -90.toRads())  }
//        sceneController.scenes += ModelScene(sceneController).apply { perspective = false; camera = camera.copy(angleX = 90.toRads(), angleY = 0.0)  }
        sceneController.scenes += SceneTexture(projectManager.modelEditor, windowHandler, sceneController)
        sceneController.refreshScenes()
    }

    override fun tick() {
        windowHandler.resetViewport()
        moduleController.tick()
        sceneController.tick()
    }
}