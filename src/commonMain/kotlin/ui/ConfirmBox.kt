package ui

import com.soywiz.korge.input.*
import com.soywiz.korge.ui.*
import com.soywiz.korge.view.*
import com.soywiz.korim.color.*
import com.soywiz.korio.async.*

inline fun Stage.confirmBox(
    msg: String,
    textYesButton: String = "Yes",
    textNoButton: String = "No",
    width: Double,
    height: Double,
    rx: Double,
    ry: Double,
    block: @ViewDslMarker ConfirmBox.() -> Unit = {}
) = ConfirmBox(this, msg, textYesButton, textNoButton, width, height, rx, ry).apply(block)

class ConfirmBox(
    stage: Stage,
    val msg: String,
    private val textYesButton: String = "Yes",
    private val textNoButton: String = "No",
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

    val onConfirm = Signal<Unit>()
    val onNoConfirm = Signal<Unit>()

    private val clickBlocker = stage.fixedSizeContainer(stage.width, stage.height)

    private val box =
        clickBlocker.roundRect(width, height, rx, ry) {
            stroke = Colors.BLACK
            strokeThickness = 4.0
            centerOn(clickBlocker)

            uiButton(textYesButton) {
                alignLeftToLeftOf(this@roundRect, textSize)
                alignBottomToBottomOf(this@roundRect, textSize)
                onClick {
                    clickBlocker.removeFromParent()
                    onConfirm()
                }
            }
            uiButton(textNoButton) {
                alignRightToRightOf(this@roundRect, textSize)
                alignBottomToBottomOf(this@roundRect, textSize)
                onClick {
                    clickBlocker.removeFromParent()
                    onNoConfirm()
                }
            }
        }

    private val msgText = box.uiText(msg) {
        textColor = Colors.BLACK
        alignLeftToLeftOf(box, textSize)
        alignTopToTopOf(box, textSize)
    }
}
