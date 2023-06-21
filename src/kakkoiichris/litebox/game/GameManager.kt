package kakkoiichris.litebox.game

import kakkoiichris.litebox.engine.AbstractGame
import kakkoiichris.litebox.engine.GameContainer
import kakkoiichris.litebox.engine.Renderer
import kakkoiichris.litebox.engine.gfx.Image
import kakkoiichris.litebox.engine.gfx.Light

fun main() {
    val gc = GameContainer(GameManager())
    
    gc.start()
}

class GameManager : AbstractGame() {
    private val background = Image("/background.png")
    private val wood = Image("/wood.png").apply {
        lightBlock = Light.FULL
        hasAlpha = true
    }
    
    private val light = Light(50, 0x00FFFF)
    
    override fun update(container: GameContainer, delta: Float) {
    }
    
    override fun render(container: GameContainer, renderer: Renderer) {
        renderer.drawImage(background, 0, 0)
        renderer.drawImage(wood, 50, 50)
        renderer.drawLight(light, container.input.mouseX, container.input.mouseY)
    }
}