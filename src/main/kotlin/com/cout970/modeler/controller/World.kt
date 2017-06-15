package com.cout970.modeler.controller

import com.cout970.modeler.api.model.IModel
import com.cout970.modeler.api.model.IObject
import com.cout970.modeler.controller.selector.Cursor
import com.cout970.modeler.controller.selector.ISelectable
import com.cout970.modeler.core.model.selection.ObjectSelection
import com.cout970.raytrace.IRayObstacle

/**
 * Created by cout970 on 2017/06/08.
 */
data class World(val models: List<IModel>, val cursor: Cursor) {

    fun getModelParts(): List<Pair<ObjectSelection, IRayObstacle>> {
        return models.firstOrNull()?.objects?.mapIndexed { index, obj ->
            ObjectSelection(index) to RayTracer.toRayObstacle(obj)
        } ?: emptyList()
    }
}

class SelectedObject(val obj: IObject) : ISelectable {
    override val hitbox: IRayObstacle = RayTracer.toRayObstacle(obj)
}