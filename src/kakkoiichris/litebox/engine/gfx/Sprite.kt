package kakkoiichris.litebox.engine.gfx

import javax.imageio.ImageIO

open class Sprite {
    val width: Int
    val height: Int
    val raster: IntArray
    
    var hasAlpha = false
    var lightBlock = 1.0
    
    constructor(path: String) {
        val image = ImageIO.read(javaClass.getResourceAsStream(path))
        
        width = image.width
        height = image.height
        raster = image.getRGB(0, 0, width, height, null, 0, width)
        
        image.flush()
    }
    
    constructor(raster: IntArray, width: Int, height: Int) {
        this.raster = raster
        this.width = width
        this.height = height
    }
}