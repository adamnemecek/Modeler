package com.cout970.modeler.core.model.mesh

import com.cout970.modeler.api.model.mesh.IFaceIndex
import com.cout970.modeler.util.join

/**
 * Created by cout970 on 2017/05/07.
 */
class FaceIndex(val index: List<Pair<Int, Int>>) : IFaceIndex {

    override val pos: List<Int> get() = index.map { it.first }
    override val tex: List<Int> get() = index.map { it.second }
    override val vertexCount: Int get() = index.size

    constructor(pos: List<Int>, tex: List<Int>) : this(pos join tex)
}