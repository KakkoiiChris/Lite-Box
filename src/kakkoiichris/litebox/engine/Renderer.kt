package kakkoiichris.litebox.engine

import kakkoiichris.litebox.engine.gfx.*
import java.awt.image.DataBufferInt
import java.lang.Integer.max
import kotlin.math.abs

class Renderer(gc: GameContainer) {
    var font = Font.COMIC
    
    private val imageRequests = mutableListOf<ImageRequest>()
    private val lightRequests = mutableListOf<LightRequest>()
    
    private val pW = gc.width
    private val pH = gc.height
    private val p = (gc.window.image.raster.dataBuffer as DataBufferInt).data
    private val zb = IntArray(p.size)
    private val lm = IntArray(p.size)
    private val lb = IntArray(p.size)
    
    private val ambient = 0xFF123456.toInt()
    
    var zDepth = 0
    
    fun clear() {
        p.fill(0)
        zb.fill(0)
        lm.fill(ambient)
    }
    
    fun process() {
        if (imageRequests.isNotEmpty()) {
            imageRequests.sortBy { it.zDepth }
            
            var i = 0
            
            while (i < imageRequests.size) {
                val ir = imageRequests[i]
                
                zDepth = ir.zDepth
                ir.image.alpha = false
                drawImage(ir.image, ir.offX, ir.offY)
                ir.image.alpha = true
                
                i++
            }
            
            imageRequests.clear()
        }
        
        if (lightRequests.isNotEmpty()) {
            var i = 0
            
            while (i < lightRequests.size) {
                drawLightRequest(lightRequests[i++])
            }
            
            lightRequests.clear()
        }
        
        for (i in p.indices) {
            val lr = ((lm[i] shr 16) and 0xFF) / 255f
            val lg = ((lm[i] shr 8) and 0xFF) / 255f
            val lb = (lm[i] and 0xFF) / 255f
            
            val pr = ((p[i] shr 16) and 0xFF) / 255f
            val pg = ((p[i] shr 8) and 0xFF) / 255f
            val pb = (p[i] and 0xFF) / 255f
            
            val r = ((lr * pr) * 255).toInt()
            val g = ((lg * pg) * 255).toInt()
            val b = ((lb * pb) * 255).toInt()
            
            p[i] = (r shl 16) or (g shl 8) or b
        }
    }
    
    fun setPixel(x: Int, y: Int, value: Int, block: Int) {
        val alpha = value shr 24 and 0xFF
        
        if (x !in 0 until pW || y !in 0 until pH || alpha == 0) return
        
        val index = x + y * pW
        
        if (zb[index] > zDepth) return
        
        zb[index] = zDepth
        
        if (alpha == 255) {
            p[index] = value
        }
        else {
            val pixelColor = p[index]
            
            val newRed = (pixelColor shr 16 and 0xFF) - (((pixelColor shr 16 and 0xFF) - (value shr 16 and 0xFF)) * (alpha / 255.0)).toInt()
            val newGreen = (pixelColor shr 8 and 0xFF) - (((pixelColor shr 8 and 0xFF) - (value shr 8 and 0xFF)) * (alpha / 255.0)).toInt()
            val newBlue = (pixelColor and 0xFF) - (((pixelColor and 0xFF) - (value and 0xFF)) * (alpha / 255.0)).toInt()
            
            p[index] = (0xFF shl 24) or (newRed shl 16) or (newGreen shl 8) or newBlue
        }
        
        lb[index] = block
    }
    
    fun setLightMap(x: Int, y: Int, value: Int) {
        if (x !in 0 until pW || y !in 0 until pH) return
        
        val index = x + y * pW
        
        val baseColor = lm[index]
        
        val maxRed = max((value shr 16) and 0xFF, (baseColor shr 16) and 0xFF)
        val maxGreen = max((value shr 8) and 0xFF, (baseColor shr 8) and 0xFF)
        val maxBlue = max(value and 0xFF, baseColor and 0xFF)
        
        lm[index] = (maxRed shl 16) or (maxGreen shl 8) or maxBlue
    }
    
    fun drawLight(light: Light, x: Int, y: Int) {
        lightRequests += LightRequest(light, x, y)
    }
    
    private fun drawLightRequest(lightRequest: LightRequest) {
        val (light, x, y) = lightRequest
        
        for (i in 0..light.diameter) {
            drawLightLine(light, light.radius, light.radius, i, 0, x, y)
            drawLightLine(light, light.radius, light.radius, i, light.diameter, x, y)
            drawLightLine(light, light.radius, light.radius, 0, i, x, y)
            drawLightLine(light, light.radius, light.radius, light.diameter, i, x, y)
        }
    }
    
    private fun drawLightLine(light: Light, x0: Int, y0: Int, x1: Int, y1: Int, offX: Int, offY: Int) {
        var x = x0
        var y = y0
        
        val dx = abs(x1 - x0)
        val dy = abs(y1 - y0)
        
        val sx = if (x0 < x1) 1 else -1
        val sy = if (y0 < y1) 1 else -1
        
        var err = dx - dy
        
        while (true) {
            val screenX = x - light.radius + offX
            val screenY = y - light.radius + offY
            
            if (screenX !in 0 until pW || screenY !in 0 until pH) return
            
            val lightColor = light[x, y]
            
            if (lightColor == 0) return
            
            if (lb[screenX + screenY * pW] == Light.FULL) return
            
            setLightMap(screenX, screenY, light[x, y])
            
            if (x == x1 && y == y1) break
            
            val e2 = 2 * err;
            
            if (e2 > -1 * dy) {
                err -= dy;
                x += sx;
            }
            
            if (e2 < dx) {
                err += dx
                y += sy
            }
        }
    }
    
    fun drawText(text: String, offX: Int, offY: Int, color: Int) {
        val fontImage = font.fontImage
        
        var offset = 0
        
        for (i in text.indices) {
            val unicode = text[i].toInt()
            
            for (y in 0 until fontImage.h) {
                for (x in 0 until font.widths[unicode]) {
                    if (fontImage.p[(x + font.offsets[unicode]) + y * fontImage.w] == 0xFFFFFFFF.toInt()) {
                        setPixel(x + offX + offset, y + offY, color, Light.NONE)
                    }
                }
            }
            
            offset += font.widths[unicode]
        }
    }
    
    fun drawImage(image: Image, offX: Int, offY: Int) {
        if (image.alpha) {
            imageRequests.add(ImageRequest(image, zDepth, offX, offY))
            
            return
        }
        
        if (offX < -image.w) return
        if (offY < -image.h) return
        if (offX >= pW) return
        if (offY >= pH) return
        
        var newX = 0
        var newY = 0
        var newW = image.w
        var newH = image.h
        
        if (offX < 0) newX -= offX
        if (offY < 0) newY -= offY
        if (offX + newW > pW) newW -= (newW + offX - pW)
        if (offY + newH > pH) newH -= (newH + offY - pH)
        
        for (y in newY until newH) {
            for (x in newX until newW) {
                setPixel(x + offX, y + offY, image.p[x + y * image.w], image.block)
            }
        }
    }
    
    fun drawImageTile(image: ImageTile, offX: Int, offY: Int, tileX: Int, tileY: Int) {
        if (image.alpha) {
            imageRequests.add(ImageRequest(image.getTileImage(tileX, tileY), zDepth, offX, offY))
            
            return
        }
        
        if (offX < -image.tileW) return
        if (offY < -image.tileH) return
        if (offX >= pW) return
        if (offY >= pH) return
        
        var newX = 0
        var newY = 0
        var newW = image.tileW
        var newH = image.tileH
        
        if (offX < 0) newX -= offX
        if (offY < 0) newY -= offY
        if (offX + newW > pW) newW -= (newW + offX - pW)
        if (offY + newH > pH) newH -= (newH + offY - pH)
        
        for (y in newY until newH) {
            for (x in newX until newW) {
                setPixel(x + offX, y + offY, image.p[(x + tileX * image.tileW) + (y + tileY * image.tileH) * image.w], image.block)
            }
        }
    }
    
    fun drawRect(offX: Int, offY: Int, width: Int, height: Int, color: Int) {
        for (y in 0 until height) {
            setPixel(offX, y + offY, color, Light.NONE)
            setPixel(offX + width - 1, y + offY, color, Light.NONE)
        }
        
        for (x in 0 until width) {
            setPixel(x + offX, offY, color, Light.NONE)
            setPixel(x + offX, offY + height - 1, color, Light.NONE)
        }
    }
    
    fun fillRect(offX: Int, offY: Int, width: Int, height: Int, color: Int) {
        if (offX < -width) return
        if (offY < -height) return
        if (offX >= pW) return
        if (offY >= pH) return
        
        var newX = 0
        var newY = 0
        var newW = width
        var newH = height
        
        if (offX < 0) newX -= offX
        if (offY < 0) newY -= offY
        if (offX + newW > pW) newW -= (newW + offX - pW)
        if (offY + newH > pH) newH -= (newH + offY - pH)
        
        for (y in newY until newH) {
            for (x in newX until newW) {
                setPixel(x + offX, y + offY, color, Light.NONE)
            }
        }
    }
}