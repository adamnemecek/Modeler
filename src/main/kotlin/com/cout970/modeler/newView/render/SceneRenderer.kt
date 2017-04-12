package com.cout970.modeler.newView.render

import com.cout970.glutilities.tessellator.VAO
import com.cout970.modeler.event.IInput
import com.cout970.modeler.modeleditor.ModelEditor
import com.cout970.modeler.newView.gui.Scene
import com.cout970.modeler.newView.render.comp.*
import com.cout970.modeler.util.Cache
import com.cout970.modeler.util.absolutePosition
import com.cout970.modeler.util.toIVector
import com.cout970.modeler.window.WindowHandler
import com.cout970.vector.extensions.vec2Of
import com.cout970.vector.extensions.yf

/**
 * Created by cout970 on 2017/01/23.
 */
class SceneRenderer(
        val shaderHandler: ShaderHandler,
        val modelEditor: ModelEditor,
        val windowHandler: WindowHandler,
        val input: IInput
) {

    val components = mapOf(
            ShaderType.MODEL_SHADER to listOf(ModelRenderComponent()),
            ShaderType.SELECTION_SHADER to listOf(
                    GridsRenderComponent(), SelectionRenderComponent(),
                    AABBRenderComponent(), Scene3dCursorRenderComponent()
            ),
            ShaderType.GUI_SHADER to listOf(GuiCursorRenderComponent()))

    val modelCache = Cache<Int, VAO>(20).apply { onRemove = { _, v -> v.close() } }
    val selectionCache = Cache<Int, VAO>(40).apply { onRemove = { _, v -> v.close() } }
    val commonCache = Cache<Int, VAO>(100).apply { onRemove = { _, v -> v.close() } }
    val volatileCache = Cache<Int, VAO>(10).apply { onRemove = { _, v -> v.close() } }

    fun render(scene: Scene) {
        if (scene.size.x < 1 || scene.size.y < 1) return

        val contentPanel = scene.contentPanel

        if (modelEditor.modelNeedRedraw) {
            modelEditor.modelNeedRedraw = false
            modelCache.clear()
            selectionCache.clear()
        }

        val context = RenderContext(
                shaderHandler = shaderHandler,
                modelProvider = modelEditor,
                model = contentPanel.selectedScene?.viewTarget?.getModel() ?: modelEditor.model,
                selectionManager = modelEditor.selectionManager,
                contentPanel = contentPanel,
                scene = scene,
                renderer = this,
                input = input
        )

        val viewportPos = vec2Of(
                scene.absolutePosition.x,
                windowHandler.window.size.yf - (scene.absolutePosition.yf + scene.size.y)
        )
        windowHandler.saveViewport(viewportPos, scene.size.toIVector()) {

            ShaderType.values().forEach {
                val comps = components[it] ?: emptyList()
                if (comps.any { it.canRender(context) }) {
                    shaderHandler.useShader(it, context) {
                        comps.forEach {
                            it.render(context)
                        }
                    }
                }
            }
        }
    }
}