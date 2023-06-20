package kakkoiichris.litebox.engine

import java.awt.BorderLayout
import java.awt.Canvas
import java.awt.Dimension
import java.awt.Graphics
import java.awt.image.BufferStrategy
import java.awt.image.BufferedImage
import javax.swing.JFrame

class Window(gc: GameContainer) {
    private val frame: JFrame
    val image: BufferedImage
    val canvas: Canvas
    private val bs: BufferStrategy
    private val g: Graphics
    
    init {
        image = BufferedImage(gc.width, gc.height, BufferedImage.TYPE_INT_RGB)
        
        canvas = Canvas()
        val s = Dimension((gc.width * gc.scale).toInt(), (gc.height * gc.scale).toInt())
        canvas.preferredSize = s
        canvas.maximumSize = s
        canvas.minimumSize = s
        
        frame = JFrame(gc.title)
        frame.defaultCloseOperation = JFrame.EXIT_ON_CLOSE
        frame.layout = BorderLayout()
        frame.add(canvas, BorderLayout.CENTER)
        frame.pack()
        frame.setLocationRelativeTo(null)
        frame.isResizable = false
        frame.isVisible = true
        
        canvas.createBufferStrategy(2)
        bs = canvas.bufferStrategy
        g = bs.drawGraphics
    }
    
    fun update() {
        g.drawImage(image, 0, 0, canvas.width, canvas.height, null)
        bs.show()
    }
}