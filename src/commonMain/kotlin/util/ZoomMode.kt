package util

import com.soywiz.korev.*
import com.soywiz.korge.view.*
import com.soywiz.korio.lang.*
import com.soywiz.korma.geom.*

fun Stage.zoomModeOn() {
    fun zoomIn() {
        scale += .05
        x = mouseX - width * scale / 2
        y = mouseY - height * scale / 2
    }
    fun zoomOut() {
        if (scale <= 1.05) {
            scale = 1.0
            x = .0
            y = .0
        } else {
            val f = (scale - 1.0) / .05
            scale -= .05
            x += if (x > .0) x / f else -x / f
            y += if (y > .0) y / f else -y / f
        }
    }
    println("Zoom on")
    val mouseZoom = addEventListener<MouseEvent> {
        when {
            it.scrollDeltaYPixels < .0 -> { println("Zoom in"); zoomIn() }
            it.scrollDeltaYPixels > .0 -> { println("Zoom out"); zoomOut() }
        }
    }
    addProp("mouseZoom", mouseZoom)
    var dStart = .0

    val touchZoom = addEventListener<TouchEvent> {
        if (it.isStart && it.touches.size ==  2) {
            val p0 = Point(it.touches[0].x, it.touches[0].y)
            val p1 = Point(it.touches[1].x, it.touches[1].y)
            dStart = p0.distanceTo(p1)
        } else if (!it.isStart && it.touches.size ==  2) {
            val p0 = Point(it.touches[0].x, it.touches[0].y)
            val p1 = Point(it.touches[1].x, it.touches[1].y)
            val dEnd = p0.distanceTo(p1)
            val d = dEnd - dStart
            when {
                d > .0 -> { println("Zoom in"); zoomIn(); dStart = dEnd }
                d < .0 -> { println("Zoom out"); zoomOut(); dStart = dEnd }
            }
        }
    }
    addProp("touchZoom", touchZoom)
}

fun Stage.zoomModeOff() {
    println("Zoom off")
    getProp<Closeable>("mouseZoom").close()
    getProp<Closeable>("touchZoom").close()
}
