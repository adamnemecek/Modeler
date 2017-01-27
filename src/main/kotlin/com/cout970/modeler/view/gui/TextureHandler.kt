package com.cout970.modeler.view.gui

import com.cout970.modeler.resource.ResourceLoader
import org.joml.Vector2f
import org.liquidengine.legui.component.ImageView

/**
 * Created by cout970 on 2017/01/24.
 */
class TextureHandler(loader: ResourceLoader) {

    val selectionModeGroup: ImageView
    val selectionModeMesh: ImageView
    val selectionModeQuad: ImageView
    val selectionModeVertex: ImageView
    val cursorTranslate: ImageView
    val cursorRotate: ImageView
    val cursorScale: ImageView

    init {
        selectionModeGroup = ImageView(
                loader.getImage("assets/textures/selection_mode_group.png")).apply { size = Vector2f(32f) }
        selectionModeMesh = ImageView(
                loader.getImage("assets/textures/selection_mode_mesh.png")).apply { size = Vector2f(32f) }
        selectionModeQuad = ImageView(
                loader.getImage("assets/textures/selection_mode_quad.png")).apply { size = Vector2f(32f) }
        selectionModeVertex = ImageView(
                loader.getImage("assets/textures/selection_mode_vertex.png")).apply { size = Vector2f(32f) }
        cursorTranslate = ImageView(loader.getImage("assets/textures/translation.png")).apply {
            size = Vector2f(16f); position = Vector2f(5f, 2f)
        }
        cursorRotate = ImageView(loader.getImage("assets/textures/rotation.png")).apply {
            size = Vector2f(16f); position = Vector2f(5f, 2f)
        }
        cursorScale = ImageView(loader.getImage("assets/textures/scale.png")).apply {
            size = Vector2f(16f); position = Vector2f(5f, 2f)
        }
    }
}