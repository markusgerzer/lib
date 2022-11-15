package ui

import com.soywiz.korge.input.*
import com.soywiz.korge.ui.*
import com.soywiz.korge.view.*
import com.soywiz.korim.color.*
import com.soywiz.korio.async.*

inline fun Stage.uiConfirmBox(
    msg: String,
    width: Double,
    height: Double,
    rx: Double,
    ry: Double,
    padding: Double = 16.0,
    textYesButton: String = "Yes",
    textNoButton: String = "No",
    block: @ViewDslMarker UIConfirmBox.() -> Unit = {}
) = UIConfirmBox(this, msg, width, height, rx, ry, padding, textYesButton, textNoButton).apply(block)

class UIConfirmBox(
    stage: Stage,
    val msg: String,
    width: Double = 300.0,
    height: Double = 100.0,
    rx: Double = 20.0,
    ry: Double = 20.0,
    padding: Double = 16.0,
    private val textYesButton: String = "Yes",
    private val textNoButton: String = "No",
): UIView(width, height) {
    var stroke
        get() = box.stroke
        set(value) { box.stroke = value }

    var strokeThickness
        get() = box.strokeThickness
        set(value) { box.strokeThickness = value }

    val onConfirm = Signal<Unit>()
    val onNoConfirm = Signal<Unit>()

    override fun <T> setSkinProperty(property: String, value: T) {
        super.setSkinProperty(property, value)
        msgText.setSkinProperty(property, value)
        yesButton.setSkinProperty(property, value)
        noButton.setSkinProperty(property, value)
    }

    private val clickBlocker = stage.fixedSizeContainer(stage.width, stage.height)

    private val box =
        clickBlocker.roundRect(width, height, rx, ry) {
            stroke = Colors.BLACK
            strokeThickness = 4.0
            centerOn(clickBlocker)
        }

    private val yesButton = UIButton(text = textYesButton).addTo(box) {
        alignLeftToLeftOf(box, padding)
        alignBottomToBottomOf(box, padding)
        onClick {
            clickBlocker.removeFromParent()
            onConfirm()
        }
    }

    private val noButton = UIButton(text = textNoButton).addTo(box) {
        alignRightToRightOf(box, padding)
        alignBottomToBottomOf(box, padding)
        onClick {
            clickBlocker.removeFromParent()
            onNoConfirm()
        }
    }

    private val msgText = box.uiText(msg) {
        textColor = Colors.BLACK
        alignLeftToLeftOf(box, padding)
        alignTopToTopOf(box, padding)
    }
}
