package util

import com.soywiz.korev.*
import com.soywiz.korge.view.*
import com.soywiz.korio.lang.*
import com.soywiz.korma.geom.*

private const val ZOOM_MODE_EVENT_LISTENER = "ZoomModeEventListener"
private const val ZOOM_STEP = .05

fun Stage.zoomModeOn() {
    fun zoomIn() {
        val mX = mouseX
        val mY = mouseY
        scale += ZOOM_STEP
        x += (mouseX - mX) * scaleX
        y += (mouseY - mY) * scaleY
    }

    fun zoomOut() {
        if (scale <= 1.0 + ZOOM_STEP) {
            scale = 1.0
            x = .0
            y = .0
        } else {
            val f = (scale - 1.0) / ZOOM_STEP
            scale -= ZOOM_STEP
            x += if (x > .0) x / f else -x / f
            y += if (y > .0) y / f else -y / f
        }
    }

    val mouseZoom = addEventListener<MouseEvent> {
        when {
            it.scrollDeltaYPixels < .0 -> zoomIn()
            it.scrollDeltaYPixels > .0 -> zoomOut()
        }
    }

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
                d > .0 -> { zoomIn(); dStart = dEnd }
                d < .0 -> { zoomOut(); dStart = dEnd }
            }
        }
    }

    var scroll = false
    var scrollX = 0
    var scrollY = 0
    val mouseScroll = addEventListener<MouseEvent> {
        if (it.type == MouseEvent.Type.DOWN /*&& it.button == MouseButton.MIDDLE*/) {
            scroll = true
            scrollX = it.x
            scrollY = it.y
        } else if (it.type == MouseEvent.Type.UP /*&& it.button == MouseButton.MIDDLE*/) {
            scroll = false
        } else if (it.type == MouseEvent.Type.DRAG && scroll) {
            val dX = it.x - scrollX
            val dY = it.y - scrollY
            scrollX = it.x
            scrollY = it.y
            val minX = width - scaledWidth
            val minY = height - scaledHeight
            x = (x + dX).coerceIn(minX .. .0)
            y = (y + dY).coerceIn(minY .. .0)
        }
    }

    val listener = listOf(mouseZoom, touchZoom, mouseScroll)
    addProp(ZOOM_MODE_EVENT_LISTENER, listener)
}

fun Stage.zoomModeOff() {
    val listener = getPropOrNull<List<Closeable>>(ZOOM_MODE_EVENT_LISTENER) ?: return
    listener.forEach(Closeable::close)
    addProp(ZOOM_MODE_EVENT_LISTENER, listOf<Closeable>())
}

val Stage.zoomMode get() = getPropOrNull<List<Closeable>>(ZOOM_MODE_EVENT_LISTENER)?.isNotEmpty() ?: false
