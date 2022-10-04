import com.soywiz.korev.*
import com.soywiz.korge.*
import com.soywiz.korge.input.*
import com.soywiz.korge.scene.*
import com.soywiz.korge.view.*
import com.soywiz.korgw.*
import com.soywiz.korim.color.*
import com.soywiz.korinject.*
import com.soywiz.korma.geom.*
import util.*
import kotlin.reflect.*

suspend fun main() = Korge(Korge.Config(module = ConfigModule))

object ConfigModule : Module() {
    override val size = SizeInt(512, 512)
    override val quality = GameWindow.Quality.PERFORMANCE
    override val bgcolor = Colors.GHOSTWHITE
    override val scaleMode = ScaleMode.SHOW_ALL
    override val clipBorders = false
    override val scaleAnchor = Anchor.BOTTOM
    override val mainScene: KClass<out Scene> = SceneA::class
    override suspend fun AsyncInjector.configure() {
        mapPrototype { SceneA() }
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
