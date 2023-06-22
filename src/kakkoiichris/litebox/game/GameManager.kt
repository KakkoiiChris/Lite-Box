package kakkoiichris.litebox.game

import kakkoiichris.litebox.engine.AbstractGame
import kakkoiichris.litebox.engine.Display
import kakkoiichris.litebox.engine.Input
import kakkoiichris.litebox.engine.Renderer
import kakkoiichris.litebox.engine.gfx.Image
import kakkoiichris.litebox.engine.gfx.Light
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

fun main() {
    val display = Display(GameManager(), scale = 3)
    
    display.open()
}

class GameManager : AbstractGame {
    private val background = Image("/background.png")
    private val block = Image("/block.png").apply {
        lightBlock = 0.9
    }
    
    private val lightCount = 12
    private val lights = Array(lightCount) { i -> Light(50, java.awt.Color.HSBtoRGB(i / lightCount.toFloat(), 1F, 1F)) }
    
    private var x = 0
    private var y = 0
    private var a = 0.0
    private var m = 0.0
    
    override fun update(display: Display, delta: Double, input: Input) {
        x = input.mouseX
        y = input.mouseY
        
        a += delta * 0.001
        m += delta * 0.01
    }
    
    override fun render(display: Display, renderer: Renderer) {
        renderer.ambientLight = 0x222222
        renderer.drawImage(background, 0, 0)
        
        renderer.drawImage(block, 0, 0)
        
        for ((i, light) in lights.withIndex()) {
            val ox = (cos((i / lights.size.toDouble() + a) * PI * 2) *sin(m)* 50).toInt()
            val oy = (sin((i / lights.size.toDouble() + a) * PI * 2) *sin(m)* 50).toInt()
            
            renderer.drawLight(light, x + ox, y + oy)
        }
    }
}