package kakkoiichris.litebox.engine

import java.awt.event.*

class Input(private val scale: Int) : KeyListener, MouseListener, MouseMotionListener, MouseWheelListener {
    private val numKeys = 256
    private val keys = Toggle.array(numKeys)
    
    private val numButtons = 4
    private val buttons = Toggle.array(numButtons)
    
    private val pollBuffer = mutableListOf<Toggle>()
    
    var mouseX = 0; private set
    
    var mouseY = 0; private set
    
    var scroll = 0; private set
    
    fun poll() {
        scroll = 0
        
        pollBuffer.forEach(Toggle::poll)
    }
    
    fun isKey(key: Int) = keys[key].held
    
    fun isKeyUp(key: Int) = keys[key].up
    
    fun isKeyDown(key: Int) = keys[key].down
    
    fun isButton(button: Int) = buttons[button].held
    
    fun isButtonUp(button: Int) = buttons[button].up
    
    fun isButtonDown(button: Int) = buttons[button].down
    
    override fun keyPressed(e: KeyEvent) {
        if (e.keyCode !in keys.indices) return
        
        val key = keys[e.keyCode]
        
        key.set(true)
        
        pollBuffer += key
    }
    
    override fun keyReleased(e: KeyEvent) {
        if (e.keyCode !in keys.indices) return
        
        val key = keys[e.keyCode]
        
        key.set(false)
        
        pollBuffer += key
    }
    
    override fun keyTyped(e: KeyEvent) = e.consume()
    
    override fun mousePressed(e: MouseEvent) {
        if (e.button !in buttons.indices) return
        
        val button = buttons[e.button]
        
        button.set(true)
        
        pollBuffer += button
    }
    
    override fun mouseReleased(e: MouseEvent) {
        if (e.button !in buttons.indices) return
        
        val button = buttons[e.button]
        
        button.set(false)
        
        pollBuffer += button
    }
    
    override fun mouseClicked(e: MouseEvent) = e.consume()
    
    override fun mouseEntered(e: MouseEvent) {}
    
    override fun mouseExited(e: MouseEvent) {}
    
    override fun mouseMoved(e: MouseEvent) {
        mouseX = e.x / scale
        mouseY = e.y / scale
    }
    
    override fun mouseDragged(e: MouseEvent) = mouseMoved(e)
    
    override fun mouseWheelMoved(e: MouseWheelEvent) {
        scroll = e.wheelRotation
    }
    
    internal class Toggle {
        private var then = false
        private var now = false
        
        val down get() = now && !then
        
        val held get() = now
        
        val up get() = !now && then
        
        fun set(down: Boolean) {
            now = down
        }
        
        fun poll() {
            then = now
        }
        
        companion object {
            fun array(size: Int) = Array(size) { Toggle() }
        }
    }
}