package kakkoiichris.litebox.engine

import java.awt.event.*

class Input(private val container: GameContainer) : KeyListener, MouseListener, MouseMotionListener, MouseWheelListener {
    private val numKeys = 256
    private val keys = BooleanArray(numKeys)
    private val keysLast = BooleanArray(numKeys)
    
    private val numButtons = 3
    private val buttons = BooleanArray(numButtons)
    private val buttonsLast = BooleanArray(numButtons)
    
    var mouseX = 0; private set
    var mouseY = 0; private set
    var scroll = 0; private set
    
    init {
        container.window.canvas.addKeyListener(this)
        container.window.canvas.addMouseListener(this)
        container.window.canvas.addMouseMotionListener(this)
        container.window.canvas.addMouseWheelListener(this)
    }
    
    fun update() {
        scroll = 0
        
        for (i in keys.indices) {
            keysLast[i] = keys[i]
        }
        
        for (i in buttons.indices) {
            buttonsLast[i] = buttons[i]
        }
    }
    
    fun isKey(keycode: Int) = keys[keycode]
    
    fun isKeyUp(keycode: Int) = !keys[keycode] && keysLast[keycode]
    
    fun isKeyDown(keycode: Int) = keys[keycode] && !keysLast[keycode]
    
    fun isButton(button: Int) = buttons[button]
    
    fun isButtonUp(button: Int) = !buttons[button] && buttonsLast[button]
    
    fun isButtonDown(button: Int) = buttons[button] && !buttonsLast[button]
    
    override fun keyTyped(e: KeyEvent) {}
    
    override fun keyPressed(e: KeyEvent) {
        keys[e.keyCode] = true
    }
    
    override fun keyReleased(e: KeyEvent) {
        keys[e.keyCode] = false
    }
    
    override fun mouseClicked(e: MouseEvent) {}
    
    override fun mousePressed(e: MouseEvent) {
        buttons[e.button] = true
    }
    
    override fun mouseReleased(e: MouseEvent) {
        buttons[e.button] = false
    }
    
    override fun mouseEntered(e: MouseEvent) {}
    
    override fun mouseExited(e: MouseEvent) {}
    
    override fun mouseMoved(e: MouseEvent) {
        mouseX = (e.x / container.scale).toInt()
        mouseY = (e.y / container.scale).toInt()
    }
    
    override fun mouseDragged(e: MouseEvent) = mouseMoved(e)
    
    override fun mouseWheelMoved(e: MouseWheelEvent) {
        scroll = e.wheelRotation
    }
}