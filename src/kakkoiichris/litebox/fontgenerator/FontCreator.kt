package kakkoiichris.litebox.fontgenerator

import java.awt.Color
import java.awt.EventQueue
import java.awt.Font
import java.awt.GraphicsEnvironment
import java.awt.image.BufferedImage
import java.io.File
import java.util.*
import javax.imageio.ImageIO
import javax.swing.*
import javax.swing.border.EmptyBorder
import javax.swing.filechooser.FileNameExtensionFilter

fun main() {
    EventQueue.invokeLater {
        try {
            FontCreator.isVisible = true
        }
        catch (e: Exception) {
            e.printStackTrace()
        }
    }
}

object FontCreator : JFrame() {
    private val nameComboBox: JComboBox<String>
    private val sizeComboBox: JComboBox<Int>
    
    /**
     * Create the frame.
     */
    init {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName())
        }
        catch (ignored: Exception) {
        }
        
        isResizable = false
        title = "Majoolwip Game Engine Font Creator"
        defaultCloseOperation = EXIT_ON_CLOSE
        setBounds(100, 100, 296, 136)
        setLocationRelativeTo(null)
        
        val contentPane = JPanel()
        
        contentPane.border = EmptyBorder(5, 5, 5, 5)
        contentPane.layout = null
        
        setContentPane(contentPane)
        
        val nameLabel = JLabel("Choose Font:")
        
        nameLabel.setBounds(10, 11, 200, 14)
        
        contentPane.add(nameLabel)
        
        nameComboBox = JComboBox<String>()
        
        nameComboBox.isEditable = true
        nameComboBox.setBounds(10, 28, 200, 20)
        
        contentPane.add(nameComboBox)
        
        val e = GraphicsEnvironment.getLocalGraphicsEnvironment()
        
        val fonts = e.allFonts
        
        for (font in fonts) {
            nameComboBox.addItem(font.name)
        }
        
        nameComboBox.selectedItem = "Liberation Serif"
        
        val sizeLabel = JLabel("Choose Size:")
        
        sizeLabel.setBounds(220, 11, 60, 14)
        
        contentPane.add(sizeLabel)
        
        sizeComboBox = JComboBox(Vector(listOf(8, 9, 10, 11, 12, 14, 16, 18, 20, 22, 24, 26, 28, 36, 48, 72)))
        
        sizeComboBox.isEditable = true
        sizeComboBox.setBounds(220, 28, 60, 20)
        sizeComboBox.selectedItem = 11
        
        contentPane.add(sizeComboBox)
        
        val generateButton = JButton("Create sprite!")
        
        generateButton.font = Font(generateButton.font.name, generateButton.font.style, 12)
        generateButton.setBounds(10, 59, 270, 35)
        generateButton.addActionListener { generate() }
        
        contentPane.add(generateButton)
    }
    
    private fun fontExists(name: String): Boolean {
        val e = GraphicsEnvironment.getLocalGraphicsEnvironment()
        
        val fonts = e.allFonts
        
        return fonts.any { it.name == name }
    }
    
    private fun generate() { // Check if font and size are valid
        val fontName = nameComboBox.selectedItem as String
        
        if (!fontExists(fontName)) {
            return
        }
        
        var fontSize = sizeComboBox.selectedItem as Int
        
        if (fontSize < 8) {
            return
        }
        
        // Create Font instance
        val font = Font(fontName, Font.PLAIN, fontSize)
        
        // Create an instance of Graphics2D
        val img = BufferedImage(1, 1, BufferedImage.TYPE_INT_RGB)
        val gr = img.createGraphics()
        
        // Calculate width of sprite
        var width = 0
        val metrics = getFontMetrics(font)
        
        for (c in 0..255) {
            width += metrics.charWidth(c.toChar()) + 2
        }
        
        // Calculate height of sprite
        val frc = gr.fontRenderContext
        
        val buffer = StringBuilder(256)
        
        for (c in 0..255) {
            buffer.append(c.toChar())
        }
        
        val vec = font.createGlyphVector(frc, buffer.toString())
        
        val bounds = vec.getPixelBounds(null, 0f, 0f)
        val height = bounds.height + 1
        
        // Calculate offset of font due to wrong height of chars like '(', ')' or 'Q'
        val sizes = IntArray(256) { char ->
            val gv = font.createGlyphVector(frc, char.toChar().toString())
            
            gv.getPixelBounds(null, 0f, 0f).height
        }
        
        val total = sizes.sum()
        val ratio = total / sizes.size
        
        fontSize = (fontSize + ratio) / 2
        
        // Create sprite image and get the Graphics to draw
        val sprite = BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB)
        
        val graphics = sprite.createGraphics()
        
        graphics.color = Color(0, 0, 0, 0)
        graphics.fillRect(0, 0, width, height)
        graphics.font = font
        
        // "Drawer" position
        var x = 0
        
        for (c in 0..255) {
            var y = 0
            
            graphics.color = Color.BLUE
            graphics.drawLine(x, y, x, y)
            
            x++
            y = 1
            
            graphics.color = Color.WHITE
            graphics.drawString(c.toChar().toString(), x, y + fontSize)
            
            x += metrics.charWidth(c.toChar())
            y = 0
            
            graphics.color = Color.YELLOW
            graphics.drawLine(x, y, x, y)
            
            x++
        }
        
        val result = SaveFileChooser.showSaveDialog(this)
        
        if (result == JFileChooser.APPROVE_OPTION) {
            val file = SaveFileChooser.selectedFile ?: return
            
            try {
                ImageIO.write(sprite, "png", file)
            }
            catch (ex: Exception) {
                JOptionPane.showMessageDialog(this, ex.message, ex.javaClass.canonicalName, JOptionPane.ERROR_MESSAGE)
                
                ex.printStackTrace()
                
                return
            }
            
            JOptionPane.showMessageDialog(this, "Font successfully saved at " + file.absolutePath, "File saved", JOptionPane.INFORMATION_MESSAGE)
        }
    }
    
    private object SaveFileChooser : JFileChooser() {
        init {
            dialogTitle = "Save file"
            fileFilter = FileNameExtensionFilter("Portable Network Graphics (.png)", "png")
        }
        
        override fun getSelectedFile(): File? {
            val file = super.getSelectedFile() ?: return null
            
            var fileName = file.name
            
            if (!fileName.endsWith(".png")) {
                fileName += ".png"
            }
            
            return File(file.parentFile, fileName)
        }
        
        override fun approveSelection() {
            val file = selectedFile ?: return
            
            if (file.exists() && dialogType == SAVE_DIALOG) {
                val message = "The file ${file.name} already exists. Do you want to replace the existing file?"
                val title = "Overwrite file"
                
                when (JOptionPane.showConfirmDialog(this, message, title, JOptionPane.YES_NO_CANCEL_OPTION)) {
                    JOptionPane.YES_OPTION                           -> {
                        super.approveSelection()
                        
                        return
                    }
                    
                    JOptionPane.NO_OPTION, JOptionPane.CLOSED_OPTION -> return
                    
                    JOptionPane.CANCEL_OPTION                        -> {
                        cancelSelection()
                        
                        return
                    }
                }
            }
            
            super.approveSelection()
        }
    }
}