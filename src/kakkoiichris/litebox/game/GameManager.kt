package kakkoiichris.litebox.game

import kakkoiichris.litebox.engine.Display
import kakkoiichris.litebox.engine.Input
import kakkoiichris.litebox.engine.Renderer
import kakkoiichris.litebox.engine.State
import kakkoiichris.litebox.engine.gfx.Light
import kakkoiichris.litebox.engine.gfx.Sprite
import java.awt.event.KeyEvent
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

fun main() {
    val display = Display(scale = 3)
    
    display.pushState(LightTest)
    
    display.open()
}

object LightTest : State {
    private val background = Sprite("/background.png")
    private val block = Sprite("/block.png").apply {
        lightBlock = 0.9
    }
    
    private val light = Light(200, 0xFFFFEF)
    
    private var x = 0.0
    private var y = 0.0
    private var a = 0.0
    private var m = 0.0
    
    override fun enter(display: Display) {
    }
    
    override fun leave(display: Display) {
    }
    
    override fun update(display: Display, delta: Double, input: Input) {
        if (input.isKey(KeyEvent.VK_W)) {
            y -= delta * 1
        }
        
        if (input.isKey(KeyEvent.VK_S)) {
            y += delta * 1
        }
        
        if (input.isKey(KeyEvent.VK_A)) {
            x -= delta * 1
        }
        
        if (input.isKey(KeyEvent.VK_D)) {
            x += delta * 1
        }
        
        a += delta * 0.001
        m += delta * 0.01
        
        if (input.isKeyDown(KeyEvent.VK_SPACE)) display.pushState(GameManager)
    }
    
    override fun render(display: Display, renderer: Renderer) {
        renderer.ambientLight = 0x222222
        
        renderer.drawSprite(background, 0, 0)
        
        renderer.drawSprite(block, 0, 0)
        
        renderer.drawLight(light, x.toInt(), y.toInt())
    }
}

object GameManager : State {
    private val background = Sprite("/background.png")
    private val block = Sprite("/block.png").apply {
        lightBlock = 0.9
    }
    
    private val lightCount = 12
    private val lights = Array(lightCount) { i -> Light(50, java.awt.Color.HSBtoRGB(i / lightCount.toFloat(), 1F, 1F)) }
    
    private var x = 0
    private var y = 0
    private var a = 0.0
    private var m = 0.0
    
    override fun enter(display: Display) {
    }
    
    override fun leave(display: Display) {
    }
    
    override fun update(display: Display, delta: Double, input: Input) {
        x = input.mouseX
        y = input.mouseY
        
        a += delta * 0.001
        m += delta * 0.01
        
        if (input.isKeyDown(KeyEvent.VK_SPACE)) display.popState()
    }
    
    override fun render(display: Display, renderer: Renderer) {
        renderer.ambientLight = 0x222222
        renderer.drawSprite(background, 0, 0)
        
        renderer.drawSprite(block, 0, 0)
        
        for ((i, light) in lights.withIndex()) {
            val ox = (cos((i / lights.size.toDouble() + a) * PI * 2) * sin(m) * 50).toInt()
            val oy = (sin((i / lights.size.toDouble() + a) * PI * 2) * sin(m) * 50).toInt()
            
            renderer.drawLight(light, x + ox, y + oy)
        }
    }
}