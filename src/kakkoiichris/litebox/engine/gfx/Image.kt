package kakkoiichris.litebox.engine.gfx

import javax.imageio.ImageIO

open class Image {
    val w: Int
    val h: Int
    val p: IntArray
    
    var alpha = false
    var block = Light.NONE
    
    constructor(path: String) {
        val image = ImageIO.read(javaClass.getResourceAsStream(path))
        
        w = image.width
        h = image.height
        p = image.getRGB(0, 0, w, h, null, 0, w)
        
        image.flush()
    }
    
    constructor(p: IntArray, w: Int, h: Int) {
        this.p = p
        this.w = w
        this.h = h
    }
}