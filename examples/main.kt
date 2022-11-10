import com.soywiz.klock.*
import com.soywiz.korev.*
import com.soywiz.korge.*
import com.soywiz.korge.input.*
import com.soywiz.korge.scene.*
import com.soywiz.korge.view.*
import com.soywiz.korgw.*
import com.soywiz.korim.color.*
import com.soywiz.korinject.*
import com.soywiz.korio.async.*
import com.soywiz.korma.geom.*
import kotlinx.coroutines.*
import stageZoom.*
import ui.*
import zoom.*
import kotlin.reflect.*

suspend fun main() = Korge(Korge.Config(module = ConfigModule))

object ConfigModule : Module() {
    override val size = SizeInt(512, 512)
    override val quality = GameWindow.Quality.PERFORMANCE
    override val bgcolor = Colors.GHOSTWHITE
    override val scaleMode = ScaleMode.SHOW_ALL
    override val clipBorders = false
    override val scaleAnchor = Anchor.BOTTOM
    //override val mainScene: KClass<out Scene> = SceneA::class
    //override val mainScene: KClass<out Scene> = SceneB::class
    override val mainScene: KClass<out Scene> = SceneC::class
    //override val mainScene: KClass<out Scene> = SceneD::class
    override suspend fun AsyncInjector.configure() {
        mapPrototype { SceneA() }
        mapPrototype { SceneB() }
        mapPrototype { SceneC() }
        mapPrototype { SceneD() }
    }
}

class SceneA : Scene() {
    override suspend fun SContainer.sceneInit() {
        val gridSize = 9
        val boxWidth = width / gridSize
        val boxHeight = height / gridSize
        val boxColors = listOf(Colors.DARKGRAY, Colors.DARKSEAGREEN)
        for (row in 0 until gridSize) {
            for (col in 0 until gridSize) {
                val colorIndex = (row * gridSize + col) % boxColors.size
                solidRect(boxWidth, boxHeight, boxColors[colorIndex]).xy(col * boxWidth, row * boxHeight)
            }
        }
        solidRect(10.0, 10.0, Colors.BLACK).xy(.0, .0)
        solidRect(10.0, 10.0, Colors.BLACK).xy(.0, scaledWidth - 10.0)
        solidRect(10.0, 10.0, Colors.BLACK).xy(scaledWidth - 10.0, .0)
        solidRect(10.0, 10.0, Colors.BLACK).xy(scaledWidth - 10.0, scaledWidth - 10.0)
        circle(5.0) {
            color = Colors.BLUE
            centerOnStage()
        }

        stage?.zoomModeOn()

        keys {
            this.down(Key.SPACE) {
                stage?.let{
                    if (it.zoomMode) it.zoomModeOff()
                    else it.zoomModeOn()
                }
            }
        }
    }
}

class SceneB : Scene() {
    override suspend fun SContainer.sceneInit() {
        val gridSize = 9
        val boxWidth = width / gridSize
        val boxHeight = height / gridSize
        for (row in 0 until gridSize) {
            for (col in 0 until gridSize) {
                solidRect(boxWidth, 1.0, Colors.BLUE) {
                    onOver { color = Colors.VIOLET }
                    onOut { color = Colors.BLUE }
                }.xy(col * boxWidth, row * boxHeight)
                solidRect(1.0, boxHeight, Colors.BLUE) {
                    onOver { color = Colors.VIOLET }
                    onOut { color = Colors.BLUE }
                }.xy(col * boxWidth, row * boxHeight)
            }
        }
        solidRect(10.0, 10.0, Colors.BLACK).xy(.0, .0)
        solidRect(10.0, 10.0, Colors.BLACK).xy(.0, scaledWidth - 10.0)
        solidRect(10.0, 10.0, Colors.BLACK).xy(scaledWidth - 10.0, .0)
        solidRect(10.0, 10.0, Colors.BLACK).xy(scaledWidth - 10.0, scaledWidth - 10.0)
        circle(5.0) {
            color = Colors.BLUE
            centerOnStage()
        }
        val zoomComponent = addZoomComponent(ZoomComponent(this))
        //removeZoomComponent(zoomComponent)
    }
}

class SceneC : Scene() {
    override suspend fun SContainer.sceneMain() {
        while (true) {
            val job = launchImmediately(Dispatchers.Default) {
                stage?.confirmBox("Message....", 300.0, 100.0, 20.0, 20.0) {
                    onConfirm { println("Confirmed") }
                    onNoConfirm { println("Not confirmed") }
                }
            }
            delay(3000.milliseconds)
            job.join()
        }
    }
}

class SceneD : Scene() {
    override suspend fun SContainer.sceneMain() {

        val a = uiComboBoxArray2(boxPadding = 5.0, items = (1..6).toList(), numberOfComboBoxes = 6) {
            x = 100.0
            y = 50.0

            for (i in 1 until 6) deactivateComboBox(i)

            onSelectionUpdate { idx ->
                println("a selected ${selectedItems[idx]}")
            }
        }

        uiComboBoxArray1(boxPadding = 5.0, deactivationSymbol = "-", items = listOf('A', 'B', 'C', 'D'), numberOfComboBoxes = 6) {
            x = 300.0
            y = 50.0

            onSelectionUpdate { idx ->
                println("b selected ${selectedItems[idx]}")
                if (selectedItems[idx] == null) a.deactivateComboBox(idx)
                else a.activateComboBox(idx)
            }
        }
    }
}
