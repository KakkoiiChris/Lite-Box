package kakkoiichris.litebox.engine

import java.awt.Canvas
import java.awt.Dimension
import java.awt.Frame
import java.awt.event.WindowAdapter
import java.awt.event.WindowEvent
import java.awt.image.BufferedImage

class Display(val game: AbstractGame, val width: Int = 320, val height: Int = 240, val scale: Int = 2, val title: String = "Lite Box Game Engine") : Runnable {
    private val frame = Frame(title)
    private val canvas = Canvas()
    
    private val thread: Thread
    private var running = false
    
    private val context: BufferedImage
    private val renderer: Renderer
    
    init {
        val size = Dimension(width * scale, height * scale)
        
        canvas.preferredSize = size
        canvas.maximumSize = size
        canvas.minimumSize = size
        
        frame.add(canvas)
        frame.pack()
        frame.isResizable = false
        frame.setLocationRelativeTo(null)
        frame.addWindowListener(object : WindowAdapter() {
            override fun windowClosing(e: WindowEvent) {
                running = false
            }
        })
        
        canvas.createBufferStrategy(2)
        
        thread = Thread(this, "lite_box_game_loop")
        
        context = BufferedImage(width, height, BufferedImage.TYPE_INT_RGB)
        renderer = Renderer(context)
    }
    
    fun open() {
        frame.isVisible = true
        
        running = true
        
        thread.start()
    }
    
    fun close() {
        running = false
    }
    
    override fun run() {
        val fps = 60.0
        val npu = 1E9 / fps
        
        var then = System.nanoTime()
        
        var delta = 0.0
        var timer = 0.0
        
        var updates = 0
        var frames = 0
        
        while (running) {
            val now = System.nanoTime()
            val elapsed = (now - then) / npu
            
            delta += elapsed
            timer += elapsed
            
            then = now
            
            var render = false
            
            while (delta >= 1) {
                update(delta--)
                
                updates++
                
                render = true
            }
            
            if (render) {
                render()
                
                frames++
            }
            
            if (timer >= fps) {
                frame.title = "$title: $updates U, $frames F"
                
                updates = 0
                frames = 0
                
                timer -= fps
            }
        }
        
        frame.dispose()
    }
    
    private fun update(delta: Double) {
        game.update(this, delta)
    }
    
    private fun render() {
        renderer.clear()
        
        game.render(this, renderer)
        
        renderer.process()
        
        val buffer = canvas.bufferStrategy
        
        buffer.drawGraphics.drawImage(context, 0, 0, canvas.width, canvas.height, null)
        
        buffer.show()
    }
}