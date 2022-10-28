package ui

import com.soywiz.korge.input.*
import com.soywiz.korge.ui.*
import com.soywiz.korge.view.*
import com.soywiz.korim.color.*

inline fun Stage.confirmBox(
    msg: String,
    width: Double,
    height: Double,
    rx: Double,
    ry: Double,
    block: @ViewDslMarker ConfirmBox.() -> Unit = {}
) = ConfirmBox(this, msg, width, height, rx, ry).apply(block)

class ConfirmBox(
    stage: Stage,
    val msg: String,
    width: Double = 300.0,
    height: Double = 100.0,
    rx: Double = 20.0,
    ry: Double = 20.0,
) {
    var textSize
        get() = msgText.textSize
        set(value) { msgText.textSize = value }

    var stroke
        get() = box.stroke
        set(value) { box.stroke = value }

    var strokeThickness
        get() = box.strokeThickness
        set(value) { box.strokeThickness = value }

    fun onConfirm(callback: suspend () -> Unit) = _onConfirmCallbacks.add(callback)
    private val _onConfirmCallbacks = mutableListOf<suspend () -> Unit>()
    private suspend fun confirmed() { for (callback in _onConfirmCallbacks) callback() }

    fun onNoConfirm(callback: suspend () -> Unit) = _onNoConfirmCallbacks.add(callback)
    private val _onNoConfirmCallbacks = mutableListOf<suspend () -> Unit>()
    private suspend fun notConfirmed() { for (callback in _onNoConfirmCallbacks) callback() }

    private val clickBlocker = stage.fixedSizeContainer(stage.width, stage.height)

    private val box =
        clickBlocker.roundRect(width, height, rx, ry) {
            stroke = Colors.BLACK
            centerOn(clickBlocker)

            uiButton("Yes") {
                alignLeftToLeftOf(this@roundRect, textSize)
                alignBottomToBottomOf(this@roundRect, textSize)
                onClick {
                    clickBlocker.removeFromParent()
                    confirmed()
                }
            }
            uiButton("No") {
                alignRightToRightOf(this@roundRect, textSize)
                alignBottomToBottomOf(this@roundRect, textSize)
                onClick {
                    clickBlocker.removeFromParent()
                    notConfirmed()
                }
            }
        }

    private val msgText = box.uiText(msg) {
        textColor = Colors.BLACK
        alignLeftToLeftOf(box, textSize)
        alignTopToTopOf(box, textSize)
    }
}
