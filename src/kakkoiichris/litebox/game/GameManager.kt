package kakkoiichris.litebox.game

import kakkoiichris.litebox.engine.AbstractGame
import kakkoiichris.litebox.engine.Display
import kakkoiichris.litebox.engine.Renderer
import kakkoiichris.litebox.engine.gfx.Image
import kakkoiichris.litebox.engine.gfx.Light

fun main() {
    val display = Display(GameManager())
    
    display.open()
}

class GameManager : AbstractGame() {
    private val background = Image("/background.png")
    private val wood = Image("/wood.png").apply {
        lightBlock = Light.FULL
        hasAlpha = true
    }
    
    private val light = Light(100, 0x00FFFF)
    
    private var x = 0.0
    private var y = 0.0
    
    override fun update(display: Display, delta: Double) {
        x += delta * 0.1
        y += delta * 0.11
    }
    
    override fun render(display: Display, renderer: Renderer) {
        renderer.drawImage(background, 0, 0)
        renderer.drawImage(wood, 50, 50)
        renderer.drawLight(light, x.toInt(),y.toInt())
    }
}