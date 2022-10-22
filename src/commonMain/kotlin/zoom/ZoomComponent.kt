package zoom

import com.soywiz.korev.*
import com.soywiz.korge.component.*
import com.soywiz.korge.view.*
import com.soywiz.korma.geom.*


fun View.addZoomComponent() =
    addZoomComponent(ZoomComponentMousePart(this), ZoomComponentTouchPart(this))

fun View.addZoomComponent(cm : ZoomComponentMousePart, ct : ZoomComponentTouchPart): ZoomComponent {
    addComponent(cm as MouseComponent)
    addComponent(ct as TouchComponent)
    return cm
}

fun View.removeZoomComponent(c: ZoomComponent) {
    removeComponent(c as MouseComponent)
    removeComponent(c as TouchComponent)
}

abstract class ZoomComponent(override val view: View) : Component {
    var zoomStep = .05
    var maxZoom = 5.0

    fun zoomIn(views: Views) {
        val mX = views.globalMouseX * view.scaleX
        val mY = views.globalMouseY * view.scaleY
        view.scale = (view.scale + zoomStep).coerceAtMost(maxZoom)
        view.x -= views.globalMouseX * view.scaleX - mX
        view.y -= views.globalMouseY * view.scaleY - mY
    }

    fun zoomOut() {
        if (view.scale <= 1.0 + zoomStep) {
            view.scale = 1.0
            view.x = .0
            view.y = .0
        } else {
            val f = (view.scale - 1.0) / zoomStep
            view.scale -= zoomStep
            view.x += if (view.x > .0) view.x / f else -view.x / f
            view.y += if (view.y > .0) view.y / f else -view.y / f
        }
    }

    fun scroll(dX: Int, dY: Int) {
        val minX = view.width - view.scaledWidth
        val minY = view.height - view.scaledHeight
        view.x = (view.x + dX).coerceIn(minX .. .0)
        view.y = (view.y + dY).coerceIn(minY .. .0)
    }
}

class ZoomComponentMousePart(override val view: View) : ZoomComponent(view), MouseComponent {
    private var scroll = false
    private var scrollX = 0
    private var scrollY = 0

    override fun onMouseEvent(views: Views, event: MouseEvent) {
        when {
            event.type == MouseEvent.Type.SCROLL -> {
                when {
                    event.scrollDeltaYPixels < .0 -> zoomIn(views)
                    event.scrollDeltaYPixels > .0 -> zoomOut()
                }
            }
            event.type == MouseEvent.Type.DOWN -> {
                scroll = true
                scrollX = event.x
                scrollY = event.y
            }
            event.type == MouseEvent.Type.UP -> scroll = false
            event.type == MouseEvent.Type.DRAG && scroll -> {
                val dX = event.x - scrollX
                val dY = event.y - scrollY
                scrollX = event.x
                scrollY = event.y
                scroll(dX, dY)
            }
        }
    }
}

class ZoomComponentTouchPart(override val view: View) : ZoomComponent(view), TouchComponent {
    private var dStart = .0

    override fun onTouchEvent(views: Views, e: TouchEvent) {
        if (e.isStart && e.touches.size ==  2) {
            val p0 = Point(e.touches[0].x, e.touches[0].y)
            val p1 = Point(e.touches[1].x, e.touches[1].y)
            dStart = p0.distanceTo(p1)
        } else if (!e.isStart && e.touches.size ==  2) {
            val p0 = Point(e.touches[0].x, e.touches[0].y)
            val p1 = Point(e.touches[1].x, e.touches[1].y)
            val dEnd = p0.distanceTo(p1)
            val d = dEnd - dStart
            when {
                d > .0 -> { zoomIn(views); dStart = dEnd }
                d < .0 -> { zoomOut(); dStart = dEnd }
            }
        }
    }
}
