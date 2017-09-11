package core.utils

import com.cout970.modeler.core.model.TRTSTransformation
import com.cout970.modeler.util.*
import com.cout970.vector.api.IVector3
import com.cout970.vector.extensions.Quaternion
import com.cout970.vector.extensions.vec3Of
import org.joml.Quaterniond
import org.junit.Assert
import org.junit.Test

/**
 * Created by cout970 on 2017/08/29.
 */
class TestUtilities {

    @Test
    fun `Test fromPivotToOrigin with identity`() {

        val pos = vec3Of(0)
        val rot = Quaternion.IDENTITY

        val (resPos, resRot) = (pos to rot).fromPivotToOrigin()

        assertEquals(pos, resPos)
        Assert.assertEquals(rot, resRot)
    }

    @Test
    fun `Test fromPivotToOrigin with x=1 and rot=90degrees in y`() {

        val pos = vec3Of(1, 0, 0)
        val rot = quatOfAngles(0, 90, 0)

        val (resPos, resRot) = (pos to rot).fromPivotToOrigin()

        assertEquals(vec3Of(1, 0, -1), resPos)
        Assert.assertEquals(quatOfAngles(0, 90, 0), resRot)
    }

    @Test
    fun `Test fromPivotToOrigin with z=1 and rot=180degrees in y`() {

        val pos = vec3Of(0, 0, 1)
        val rot = quatOfAngles(0, 90, 0)

        val (resPos, resRot) = (pos to rot).fromPivotToOrigin()

        assertEquals(vec3Of(1, 0, 1), resPos)
        Assert.assertEquals(quatOfAngles(0, 90, 0), resRot)
    }


    @Test
    fun `Test TRTSTransformation merge with identity`() {

        val trans = TRTSTransformation()

        Assert.assertEquals(trans, trans.merge(trans))
    }

    @Test
    fun temp(){
        val pivot = vec3Of(1,1,1)
        val rot = vec3Of(0,90,0)
        val matrix = TRTSTransformation.fromRotationPivot(pivot, rot).matrix

        val pos = vec3Of(matrix.m30d, matrix.m31d, matrix.m32d)
        val rotation = Quaterniond().setFromUnnormalized(matrix.toJOML()).toIQuaternion().toAxisRotations()
        println(matrix)
        println(pos)
        println(rotation)
    }

    fun assertEquals(vec1: IVector3, vec2: IVector3) {
        val msg = "expected:<$vec1>, but was:<$vec2>"
        val epsilon = 1.0000000116860974E-7
        Assert.assertEquals(msg, vec1.xd, vec2.xd, epsilon)
        Assert.assertEquals(msg, vec1.yd, vec2.yd, epsilon)
        Assert.assertEquals(msg, vec1.zd, vec2.zd, epsilon)
    }
}