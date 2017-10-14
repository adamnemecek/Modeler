package com.cout970.modeler.gui.react.components

import com.cout970.modeler.core.config.Config
import com.cout970.modeler.gui.canvas.CanvasContainer
import com.cout970.modeler.gui.comp.setBorderless
import com.cout970.modeler.gui.comp.setTransparent
import com.cout970.modeler.gui.react.core.RBuildContext
import com.cout970.modeler.gui.react.core.RComponent
import com.cout970.modeler.gui.react.core.RComponentSpec
import com.cout970.modeler.gui.react.leguicomp.FixedLabel
import com.cout970.modeler.gui.react.leguicomp.Panel
import com.cout970.modeler.gui.react.leguicomp.ProfilerDiagram
import com.cout970.modeler.gui.react.panel
import com.cout970.modeler.util.hide
import com.cout970.modeler.util.toColor
import org.joml.Vector2f
import org.liquidengine.legui.component.Component
import org.liquidengine.legui.component.optional.align.HorizontalAlign

/**
 * Created by cout970 on 2017/09/28.
 */
class CenterPanel : RComponent<CenterPanel.Props, Unit>() {

    init {
        state = Unit
    }

    override fun build(ctx: RBuildContext): Component = panel root@ {
        width = ctx.parentSize.xf - (if (props.leftPanelHidden) 0f else 280f) - (if (props.rightPanelHidden) 0f else 190f)
        height = ctx.parentSize.yf - 48f
        posX = (if (props.leftPanelHidden) 0f else 280f)
        posY = 48f
        setTransparent()
        setBorderless()

        val canvas = Panel().apply {
            position = Vector2f()
            size = this@root.size
            setBorderless()
            setTransparent()
            if (props.canvasContainer.canvas.isEmpty()) {
                hide()
            }
        }

        props.canvasContainer.panel = canvas
        props.canvasContainer.refreshCanvas()
        props.canvasContainer.layout.updateCanvas()

        // add canvas to root
        +canvas

        panel {
            if (canvas.isEnabled) {
                hide()
            }

            size = this@root.size
            backgroundColor = Config.colorPalette.blackColor.toColor()
            setBorderless()

            val x = size.x / 2 - 125f
            val y = size.y / 2 - 50f
            +FixedLabel("Open new view:", x, y + 0f, 100f,
                    24f).apply { textState.horizontalAlign = HorizontalAlign.LEFT; textState.fontSize = 20f }

            +FixedLabel("Close view:", x, y + 25f, 100f,
                    24f).apply { textState.horizontalAlign = HorizontalAlign.LEFT; textState.fontSize = 20f }

            +FixedLabel("Resize view:", x, y + 50f, 100f,
                    24f).apply { textState.horizontalAlign = HorizontalAlign.LEFT; textState.fontSize = 20f }

            +FixedLabel("Change mode:", x, y + 75f, 100f,
                    24f).apply { textState.horizontalAlign = HorizontalAlign.LEFT; textState.fontSize = 20f }

            +FixedLabel("Alt + N", 150f + x, y + 0f, 100f,
                    24f).apply { textState.horizontalAlign = HorizontalAlign.LEFT; textState.fontSize = 20f }

            +FixedLabel("Alt + D", 150f + x, y + 25f, 100f,
                    24f).apply { textState.horizontalAlign = HorizontalAlign.LEFT; textState.fontSize = 20f }

            +FixedLabel("Alt + J/K", 150f + x, y + 50f, 100f,
                    24f).apply { textState.horizontalAlign = HorizontalAlign.LEFT; textState.fontSize = 20f }

            +FixedLabel("Alt + M", 150f + x, y + 75f, 100f,
                    24f).apply { textState.horizontalAlign = HorizontalAlign.LEFT; textState.fontSize = 20f }

        }

        +ProfilerDiagram().apply {
            setTransparent()
            setBorderless()
            val size = Math.min(this@root.width / 2, this@root.height / 2)
            width = size + 100
            height = size
            posX = this@root.width - size
            posY = 0f

        }
    }

    class Props(val leftPanelHidden: Boolean, val rightPanelHidden: Boolean, val canvasContainer: CanvasContainer)

    companion object : RComponentSpec<CenterPanel, CenterPanel.Props, Unit>
}