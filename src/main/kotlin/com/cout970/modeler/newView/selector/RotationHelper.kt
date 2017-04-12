package com.cout970.modeler.newView.selector

import com.cout970.modeler.config.Config
import com.cout970.modeler.event.IInput
import com.cout970.modeler.newView.SceneSpaceContext
import com.cout970.modeler.util.getClosestPointOnLineSegment
import com.cout970.raytrace.Ray
import com.cout970.vector.api.IVector3
import com.cout970.vector.extensions.*

/**
 * Created by cout970 on 2017/04/08.
 */
object RotationHelper {

    fun getOffset(obj: IRotable, input: IInput, oldContext: SceneSpaceContext, newContext: SceneSpaceContext): Float {
        val change = getAngle(oldContext, newContext, obj)

        val move = Math.toDegrees(change) / 360.0 * 32 * Config.cursorRotationSpeed
        val offset: Float

        if (Config.keyBindings.disableGridMotion.check(input)) {
            offset = Math.round(move * 16) / 16f
        } else if (Config.keyBindings.disablePixelGridMotion.check(input)) {
            offset = Math.round(move * 4) / 4f
        } else {
            offset = Math.round(move).toFloat()
        }
        return Math.toRadians(offset.toDouble() * 360.0 / 32).toFloat()
    }

    fun getAngle(oldContext: SceneSpaceContext, newContext: SceneSpaceContext, obj: IRotable): Double {
        val normal = obj.normal
        val new = getDirectionToMouse(oldContext.mouseRay, obj)
        val old = getDirectionToMouse(newContext.mouseRay, obj)

        var angle = (normal cross new) angle (normal cross old)

        if (normal dot (new cross old) < 0) { // Or > 0
            angle = -angle
        }

        return angle
    }

    fun getDirectionToMouse(mouseRay: Ray, obj: IRotable): IVector3 {
        val closest = getClosestPointOnLineSegment(mouseRay.start, mouseRay.end, obj.center)
        return (closest - obj.center).normalize()
    }

    fun projectToPlane(mouseRay: Ray, obj: IRotable): IVector3 {
        val closest = getClosestPointOnLineSegment(mouseRay.start, mouseRay.end, obj.center)
        val dir = (closest - obj.center).normalize()
        val normal = (obj as CursorPartRotation).normal

        //TODO test and replace if needed
        return (normal cross dir).normalize()
        /*
            return when (obj) {
                    SelectionAxis.Z -> dir.run { Math.atan2(yd, zd) }
                    SelectionAxis.X -> dir.run { Math.atan2(-xd, zd) }
                    SelectionAxis.Y -> dir.run { Math.atan2(xd, yd) }
                    else -> 0.0
                }
     */
    }
}