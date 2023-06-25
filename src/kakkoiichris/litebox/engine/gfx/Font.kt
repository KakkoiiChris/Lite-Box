package kakkoiichris.litebox.engine.gfx

class Font(path: String) {
    companion object {
        val STANDARD = Font("/font.png")
        val COMIC = Font("/comic.png")
    }
    
    val fontSprite = Sprite(path)
    val offsets = IntArray(256)
    val widths = IntArray(256)
    
    init {
        var unicode = 0
        
        for (i in 0 until fontSprite.width) {
            if (fontSprite.raster[i] == 0xFF0000FF.toInt()) {
                offsets[unicode] = i
            }
            
            if (fontSprite.raster[i] == 0xFFFFFF00.toInt()) {
                widths[unicode] = i - offsets[unicode]
                
                unicode++
            }
        }
    }
}