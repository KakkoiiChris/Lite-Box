package kakkoiichris.litebox.engine

import java.awt.BorderLayout
import java.awt.Canvas
import java.awt.Dimension
import java.awt.Graphics
import java.awt.image.BufferStrategy
import java.awt.image.BufferedImage
import javax.swing.JFrame

class Window(container: GameContainer) {
    private val frame: JFrame
    val image: BufferedImage
    val canvas: Canvas
    private val buffer: BufferStrategy
    private val graphics: Graphics
    
    init {
        image = BufferedImage(container.width, container.height, BufferedImage.TYPE_INT_RGB)
        
        canvas = Canvas()
        val size = Dimension((container.width * container.scale).toInt(), (container.height * container.scale).toInt())
        canvas.preferredSize = size
        canvas.maximumSize = size
        canvas.minimumSize = size
        
        frame = JFrame(container.title)
        frame.defaultCloseOperation = JFrame.EXIT_ON_CLOSE
        frame.layout = BorderLayout()
        frame.add(canvas, BorderLayout.CENTER)
        frame.pack()
        frame.setLocationRelativeTo(null)
        frame.isResizable = false
        frame.isVisible = true
        
        canvas.createBufferStrategy(2)
        buffer = canvas.bufferStrategy
        graphics = buffer.drawGraphics
    }
    
    fun update() {
        graphics.drawImage(image, 0, 0, canvas.width, canvas.height, null)
        buffer.show()
    }
}