package kakkoiichris.litebox.game

import kakkoiichris.litebox.engine.AbstractGame
import kakkoiichris.litebox.engine.GameContainer
import kakkoiichris.litebox.engine.Renderer
import kakkoiichris.litebox.engine.gfx.Image
import kakkoiichris.litebox.engine.gfx.Light

class GameManager : AbstractGame() {
    val b = Image("/background.png")
    val w = Image("/wood.png").apply { block = Light.FULL;alpha = true }
    val light = Light(50, 0x00FFFF)
    
    override fun update(gc: GameContainer, dt: Float) {
    }
    
    override fun render(gc: GameContainer, r: Renderer) {
        r.drawImage(b, 0, 0)
        r.drawImage(w, 50, 50)
        r.drawLight(light, gc.input.mouseX, gc.input.mouseY)
    }
}

fun main(args: Array<String>) {
    val gc = GameContainer(GameManager())
    gc.start()
}