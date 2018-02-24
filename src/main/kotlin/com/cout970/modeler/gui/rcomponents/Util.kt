package com.cout970.modeler.gui.rcomponents

import com.cout970.modeler.gui.leguicomp.*
import com.cout970.modeler.input.window.Loop
import com.cout970.modeler.util.child
import com.cout970.modeler.util.disableInput
import com.cout970.modeler.util.text
import com.cout970.modeler.util.toJoml2f
import com.cout970.reactive.core.*
import com.cout970.reactive.dsl.*
import com.cout970.reactive.nodes.*
import com.cout970.vector.api.IVector2
import org.liquidengine.legui.component.Component
import org.liquidengine.legui.component.Panel
import org.liquidengine.legui.component.ScrollBar
import org.liquidengine.legui.component.Viewport
import org.liquidengine.legui.component.optional.Orientation
import org.liquidengine.legui.component.optional.align.HorizontalAlign
import org.liquidengine.legui.event.CursorEnterEvent
import org.liquidengine.legui.event.ScrollEvent
import org.liquidengine.legui.system.context.Context
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.*


data class FloatInputProps(
        val getter: () -> Float,
        val command: String,
        val metadata: Map<String, Any>,
        val enabled: Boolean,
        val pos: IVector2
) : RProps

private val formatter = DecimalFormat("#.###", DecimalFormatSymbols.getInstance(Locale.ENGLISH))

class FloatInput : RStatelessComponent<FloatInputProps>() {

    override fun RBuilder.render() = div("FloatInput") {
        style {
            transparent()
            borderless()
            sizeX = 75f
            sizeY = 70f
            position = props.pos.toJoml2f()
        }

        postMount {
            if (!props.enabled) {
                disableInput()
            }
        }

        comp(StringInput(null)) {
            style {
                defaultTextColor()
                background { greyColor }
                horizontalAlign = HorizontalAlign.CENTER
                fontSize = 24f
                posX = 0f
                posY = 16f
                sizeX = 75f
                sizeY = 40f
                text = formatter.format(props.getter())

                onLoseFocus = {
                    dispatch(this, 0f, text)
                }
                onEnterPress = {
                    dispatch(this, 0f, text)
                }
            }

            on<ScrollEvent<StringInput>> {
                val off = it.yoffset.toFloat()
                dispatch(it.component, off, it.component.text)
            }
        }

        comp(IconButton()) {
            style {
                sizeX = 75f
                sizeY = 16f
                icon = "button_up"
                background { lightDarkColor }
                onRelease {
                    dispatch(this, 1f, formatter.format(props.getter()))
                }
            }
        }

        comp(IconButton()) {
            style {
                posY = 56f
                sizeX = 75f
                sizeY = 16f
                icon = "button_down"
                background { lightDarkColor }
                onRelease {
                    dispatch(this, -1f, formatter.format(props.getter()))
                }
            }
        }
    }

    var lastTick = 0L

    fun dispatch(comp: Component, offset: Float, content: String) {
        // this avoid generating a million task doing the same thing
        if (lastTick == Loop.currentTick) return
        lastTick = Loop.currentTick

        comp.apply {
            metadata += props.metadata
            metadata += "offset" to offset
            metadata += "content" to content
            dispatch(props.command)
        }
    }
}


data class ScrollPanelProps(
        val postMount: Component.() -> Unit = {},
        val vertical: ScrollBar.() -> Unit = {},
        val horizontal: ScrollBar.() -> Unit = {},
        val container: DivBuilder.() -> Unit,
        val style: Panel.() -> Unit
) : RProps

data class ScrollPanelState(
        val scrollX: Float,
        val scrollY: Float
) : RState

class ScrollPanel : RComponent<ScrollPanelProps, ScrollPanelState>() {

    override fun getInitialState() = ScrollPanelState(0f, 0f)

    private var lastVerticalScrollBar: ScrollBar? = null
    private var lastHorizontalScrollBar: ScrollBar? = null
    private var leguiContext: Context? = null

    override fun RBuilder.render() = div("ScrollPanel") {
        style {
            transparent()
            borderless()
        }

        postMount {
            val vertical = child("VerticalScroll") as ScrollBar
            val horizontal = child("HorizontalScroll") as ScrollBar
            val container = child("Container") as Panel

            // Default Bar sizes, this must go before calling 'vertical' or 'horizontal' so the user can change the size
            horizontal.sizeX = 8f
            vertical.sizeY = 8f

            props.postMount.invoke(this)
            props.vertical.invoke(vertical)
            props.horizontal.invoke(horizontal)

            offsetContent(this, vertical, horizontal, container)
        }

        div("Container") {

            style {
                props.style(this)
            }

            onScroll {
                val parent = it.component.parent ?: return@onScroll
                val scroll = parent.child("VerticalScroll") as? ScrollBar ?: return@onScroll

                if (scroll.isEnabled) {
                    scroll.listenerMap.getListeners(ScrollEvent::class.java).forEach { list ->
                        list.process(ScrollEvent(scroll, it.context, it.frame, it.xoffset, it.yoffset))
                    }
                }
            }

            props.container(this)
        }

        comp(ScrollBar(), "VerticalScroll") {
            style {
                orientation = Orientation.VERTICAL
                isTabFocusable = false
                borderless()

                curValue = state.scrollY
                isScrolling = lastVerticalScrollBar?.isScrolling ?: false
                leguiContext?.let {
                    if (it.focusedGui == lastHorizontalScrollBar) {
                        it.focusedGui = this
                    }
                }
                lastVerticalScrollBar = this

                let { bar ->
                    bar.viewport = Viewport { setState { copy(scrollY = bar.curValue) } }
                }
            }

            on<CursorEnterEvent<ScrollBar>> {
                leguiContext = it.context
                if (it.isEntered) it.context.focusedGui = it.component
            }

            postMount {
                val hSize = parent.child("HorizontalScroll")!!.let { if (it.isEnabled) it.sizeY else 0f }
                posX = parent.width - sizeX
                posY = 0f
                sizeY = parent.height - hSize
            }
        }

        comp(ScrollBar(), "HorizontalScroll") {
            style {
                orientation = Orientation.HORIZONTAL
                isTabFocusable = false
                borderless()

                curValue = state.scrollX
                isScrolling = lastHorizontalScrollBar?.isScrolling ?: false
                leguiContext?.let {
                    if (it.focusedGui == lastHorizontalScrollBar) {
                        it.focusedGui = this
                    }
                }
                lastHorizontalScrollBar = this

                let { bar ->
                    bar.viewport = Viewport { setState { copy(scrollX = bar.curValue) } }
                }
            }

            on<CursorEnterEvent<ScrollBar>> {
                leguiContext = it.context
                if (it.isEntered) it.context.focusedGui = it.component
            }


            postMount {
                val vSize = parent.child("VerticalScroll")!!.let { if (it.isEnabled) it.sizeX else 0f }
                posX = 0f
                posY = parent.height - sizeY
                sizeX = parent.width - vSize
            }
        }
    }

    fun offsetContent(parent: Component, vertical: ScrollBar, horizontal: ScrollBar, container: Panel) {
        val realSizeX = container.childs.map { it.posX + it.sizeX }.max() ?: 0f
        val realSizeY = container.childs.map { it.posY + it.sizeY }.max() ?: 0f

        container.let {
            it.sizeX = parent.sizeX - vertical.let { if (it.isEnabled) it.sizeX else 0f }
            it.sizeY = parent.sizeY - horizontal.let { if (it.isEnabled) it.sizeY else 0f }
        }

        val offsetX = if (realSizeX > container.sizeX) -(realSizeX - container.sizeX) * (state.scrollX / vertical.maxValue) else 0f
        val offsetY = if (realSizeY > container.sizeY) -(realSizeY - container.sizeY) * (state.scrollY / horizontal.maxValue) else 0f

        container.forEach {
            it.posX += offsetX
            it.posY += offsetY
        }
    }
}

class ScrollPanelBuilder() {

    private var postMount: Component.() -> Unit = {}
    private var vertical: ScrollBar.() -> Unit = {}
    private var horizontal: ScrollBar.() -> Unit = {}
    private var container: DivBuilder.() -> Unit = {}
    private var style: Panel.() -> Unit = {}

    fun postMount(func: Component.() -> Unit) {
        postMount = func
    }

    fun style(func: Panel.() -> Unit) {
        style = func
    }

    fun container(func: DivBuilder.() -> Unit) {
        container = func
    }

    fun verticalScroll(func: ScrollBar.() -> Unit) {
        vertical = func
    }

    fun horizontalScroll(func: ScrollBar.() -> Unit) {
        horizontal = func
    }

    fun buildProps() = ScrollPanelProps(postMount, vertical, horizontal, container, style)
}

fun RBuilder.scrollPanel(key: String? = null, block: ScrollPanelBuilder.() -> Unit = {}) =
        child(ScrollPanel::class, ScrollPanelBuilder().apply(block).buildProps())