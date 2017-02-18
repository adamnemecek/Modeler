package com.cout970.modeler.model

import com.cout970.vector.api.IQuaternion
import com.cout970.vector.api.IVector3
import com.cout970.vector.extensions.*
import java.io.File
import java.io.FileOutputStream
import java.io.Writer
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.*

/**
 * Created by cout970 on 2017/01/31.
 */
class AABB(a: IVector3, b: IVector3) {

    val min: IVector3 = a.min(b)
    val max: IVector3 = a.max(b)

    val minX get() = min.xd
    val minY get() = min.yd
    val minZ get() = min.zd

    val maxX get() = max.xd
    val maxY get() = max.yd
    val maxZ get() = max.zd

    override fun toString(): String {
        return "AABB(min=$min, max=$max)"
    }

    fun translate(a: IVector3): AABB = AABB(min + a, max + a)

    fun rotate(rot: IQuaternion): AABB = AABB(rot.rotate(min).round(), rot.rotate(max).round())

    companion object {
        fun export(list: List<AABB>, output: File) {
            val stream = FileOutputStream(output)
            val f = DecimalFormat("0.000", DecimalFormatSymbols.getInstance(Locale.US))

            stream.use {
                val writer = stream.writer()

                writer.print("listOf(\n")
                for (i in list.take(list.size - 1)) {
                    writer.print("Vec3d(${f.format(i.min.x)}, ${f.format(i.min.y)}, ${f.format(
                            i.min.z)}) * PIXEL ${"to Vec3d(" + f.format(i.max.x) + ", " + f.format(
                            i.max.y) + ", " + f.format(i.max.z) + ") * PIXEL,\n"}")
                }
                val i = list.last()
                writer.print("Vec3d(${f.format(i.min.x)}, ${f.format(i.min.y)}, ${f.format(
                        i.min.z)}) * PIXEL ${"to Vec3d(" + f.format(i.max.x) + ", " + f.format(
                        i.max.y) + ", " + f.format(i.max.z) + ") * PIXEL\n"}")

                writer.print(")\n")
                writer.flush()
            }
        }

        fun Writer.print(str: String) {
            System.out.print(str)
            append(str)
        }
    }
}