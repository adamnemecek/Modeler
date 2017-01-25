package com.cout970.modeler.view.module

import com.cout970.modeler.view.controller.ModuleController
import com.cout970.modeler.view.gui.comp.CButton
import org.liquidengine.legui.event.component.MouseClickEvent

/**
 * Created by cout970 on 2016/12/27.
 */
class ModuleHistoric(controller: ModuleController) : Module(controller, "History") {

    init {
        addSubComponent(CButton("Undo", 10f, 0f, 80f, 20f).apply {
            leguiEventListeners.addListener(MouseClickEvent::class.java, buttonListener("menu.history.undo"))
        })
        addSubComponent(CButton("Redo", 90f, 0f, 80f, 20f).apply {
            leguiEventListeners.addListener(MouseClickEvent::class.java, buttonListener("menu.history.redo"))
        })
        addSubComponent(CButton("Copy", 10f, 25f, 50f, 20f).apply {
            leguiEventListeners.addListener(MouseClickEvent::class.java, buttonListener("menu.clipboard.copy"))
        })
        addSubComponent(CButton("Cut", 65f, 25f, 50f, 20f).apply {
            leguiEventListeners.addListener(MouseClickEvent::class.java, buttonListener("menu.clipboard.cut"))
        })
        addSubComponent(CButton("Paste", 120f, 25f, 50f, 20f).apply {
            leguiEventListeners.addListener(MouseClickEvent::class.java, buttonListener("menu.clipboard.paste"))
        })
        maximize()
    }
}