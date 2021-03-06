package com.cout970.modeler.util

import com.cout970.modeler.api.model.mesh.IMesh
import com.cout970.raytrace.Ray
import com.cout970.raytrace.RayTraceResult
import com.cout970.vector.api.IVector2
import com.cout970.vector.api.IVector3
import com.cout970.vector.extensions.*
import org.lwjgl.PointerBuffer
import org.lwjgl.system.MemoryUtil
import java.awt.Color

/**
 * Created by cout970 on 2016/12/09.
 */

fun <K, V> Map<K, V>.replace(key: K, newValue: V): Map<K, V> {
    return mapValues { (thisKey, thisValue) ->
        if (key == thisKey) newValue else thisValue
    }
}

inline fun <T> Iterable<T>.replace(predicate: (T) -> Boolean, transform: (T) -> T): List<T> {
    val list = mutableListOf<T>()
    for (i in this) {
        list += if (predicate(i)) transform(i) else i
    }
    return list
}

inline fun <T, R> Iterable<T>.flatMapIndexed(transform: (index: Int, T) -> Iterable<R>): List<R> {
    val destination = mutableListOf<R>()
    var index = 0
    for (element in this) {
        val list = transform(index, element)
        destination.addAll(list)
        index++
    }
    return destination
}

inline fun <T> Iterable<T>.filterNotIndexed(predicate: (index: Int, T) -> Boolean): List<T> {
    val destination = mutableListOf<T>()
    var index = 0
    for (element in this) {
        if (!predicate(index, element)) {
            destination.add(element)
        }
        index++
    }
    return destination
}

inline fun <S, T> Iterable<T>.reduceAll(first: S, operation: (acc: S, T) -> S): S {
    var accumulator: S = first
    for (it in this) {
        accumulator = operation(accumulator, it)
    }
    return accumulator
}


fun <T> List<Pair<RayTraceResult, T>>.getClosest(ray: Ray): Pair<RayTraceResult, T>? {
    return when {
        isEmpty() -> null
        size == 1 -> first()
        else -> {
            sortedBy { it.first.hit.distance(ray.start) }.first()
        }
    }
}

fun List<RayTraceResult>.getClosest(ray: Ray): RayTraceResult? {
    return when {
        isEmpty() -> null
        size == 1 -> first()
        else -> {
            sortedBy { it.hit.distance(ray.start) }.first()
        }
    }
}

infix fun <A, B> List<A>.join(other: List<B>): List<Pair<A, B>> {
    require(size == other.size) { "Invalid list sizes: this.size = $size, other.size = ${other.size}" }
    return this zip other
}

infix fun IntArray.join(other: IntArray): List<Pair<Int, Int>> {
    require(size == other.size) { "Invalid array sizes: this.size = $size, other.size = ${other.size}" }
    return this zip other
}

fun IMesh.middle(): IVector3 = pos.middle()

fun List<IVector3>.middle(): IVector3 {
    var acum = Vector3.ORIGIN
    forEach {
        acum += it
    }
    return acum / size
}

fun List<IVector2>.middle(): IVector2 {
    var acum = Vector2.ORIGIN
    forEach {
        acum += it
    }
    return acum / size
}

fun List<String>.toPointerBuffer(): PointerBuffer {
    val pointer = MemoryUtil.memAllocPointer(size)
    forEach { pointer.put(MemoryUtil.memUTF8(it)) }
    pointer.flip()
    return pointer
}

fun <T> List<T>.combine(multi: Boolean, element: T): List<T> {
    return if (multi) {
        if (element in this) {
            this - element
        } else {
            this + element
        }
    } else {
        if (this.size == 1 && element in this) {
            emptyList()
        } else {
            listOf(element)
        }
    }
}

fun <T> List<T>.combine(multi: Boolean, elements: List<T>): List<T> {
    return if (multi) {
        if (elements.all { it in this }) {
            this - elements
        } else {
            this + elements
        }
    } else {
        if (this.size == elements.size && elements.all { it in this }) {
            emptyList()
        } else {
            elements
        }
    }
}

fun <T> List<T>.getCyclic(index: Int): T {
    val ind = index % size
    return get(if (ind < 0) ind + size else ind)
}

fun getColor(hash: Int): IVector3 {
    val hue = hash.toDouble() / Int.MAX_VALUE.toDouble()
    val c = Color.getHSBColor(hue.toFloat(), 0.5f, 1f)
    return vec3Of(c.blue / 255f, c.green / 255f, c.red / 255f)
}