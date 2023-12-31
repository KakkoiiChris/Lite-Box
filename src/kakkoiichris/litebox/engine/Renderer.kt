package kakkoiichris.litebox.engine

import kakkoiichris.litebox.engine.gfx.*
import java.awt.image.BufferedImage
import java.awt.image.DataBufferInt
import kotlin.math.abs

class Renderer(context: BufferedImage) {
    private val imageRequests = mutableListOf<ImageRequest>()
    private val lightRequests = mutableListOf<LightRequest>()
    
    private val width = context.width
    private val height = context.height
    private val raster = (context.raster.dataBuffer as DataBufferInt).data
    
    private val depthBuffer = IntArray(raster.size)
    private val lightMap = IntArray(raster.size)
    private val lightBlock = DoubleArray(raster.size)
    
    var ambientLight = 0xFF000000.toInt()
    var font = Font.COMIC
    var zDepth = 0
    
    fun clear() {
        raster.fill(0)
        depthBuffer.fill(0)
        lightMap.fill(ambientLight)
    }
    
    fun process() {
        if (imageRequests.isNotEmpty()) {
            imageRequests.sortBy { it.z }
            
            for ((image, x, y, z) in imageRequests) {
                zDepth = z
                
                image.hasAlpha = false
                
                drawSprite(image, x, y)
                
                image.hasAlpha = true
            }
            
            imageRequests.clear()
        }
        
        if (lightRequests.isNotEmpty()) {
            for(lr in lightRequests) {
                drawLightRequest(lr)
            }
            
            lightRequests.clear()
        }
        
        for (i in raster.indices) {
            val base = Color(raster[i])
            val light = Color(lightMap[i])
            
            raster[i] = (base * light).value
        }
    }
    
    private fun setPixel(x: Int, y: Int, value: Int, block: Double) {
        val index = x + y * width
        
        val src = Color(value)
        
        if (
            x !in 0 until width
            || y !in 0 until height
            || src.alpha == 0
            || depthBuffer[index] > zDepth
        ) return
        
        depthBuffer[index] = zDepth
        
        if (src.alpha == 255) {
            raster[index] = value
        }
        else {
            val dst = Color(raster[index])
            
            raster[index] = dst.blend(src).value
        }
        
        lightBlock[index] = block
    }
    
    private fun setLightMap(x: Int, y: Int, value: Int) {
        if (x !in 0 until width || y !in 0 until height) return
        
        val index = x + y * width
        
        val baseColor = Color(lightMap[index])
        val addedColor = Color(value)
        
        lightMap[index] = baseColor.max(addedColor).value
    }
    
    fun drawRect(x: Int, y: Int, width: Int, height: Int, color: Int) {
        for (py in 0 until height) {
            setPixel(x, py + y, color, 1.0)
            setPixel(x + width - 1, py + y, color, 1.0)
        }
        
        for (px in 0 until width) {
            setPixel(px + x, y, color, 1.0)
            setPixel(px + x, y + height - 1, color, 1.0)
        }
    }
    
    fun fillRect(x: Int, y: Int, width: Int, height: Int, color: Int) {
        if (x < -width) return
        if (y < -height) return
        if (x >= this.width) return
        if (y >= this.height) return
        
        var newX = 0
        var newY = 0
        var newW = width
        var newH = height
        
        if (x < 0) newX -= x
        if (y < 0) newY -= y
        if (x + newW > this.width) newW -= (newW + x - this.width)
        if (y + newH > this.height) newH -= (newH + y - this.height)
        
        for (py in newY until newH) {
            for (px in newX until newW) {
                setPixel(px + x, py + y, color, 1.0)
            }
        }
    }
    
    fun drawText(text: String, x: Int, y: Int, color: Int) {
        val fontImage = font.fontSprite
        
        var offset = 0
        
        for (i in text.indices) {
            val unicode = text[i].code
            
            for (gy in 0 until fontImage.height) {
                for (gx in 0 until font.widths[unicode]) {
                    if (fontImage.raster[(gx + font.offsets[unicode]) + gy * fontImage.width] == 0xFFFFFFFF.toInt()) {
                        setPixel(gx + x + offset, gy + y, color, 1.0)
                    }
                }
            }
            
            offset += font.widths[unicode]
        }
    }
    
    fun drawSprite(sprite: Sprite, x: Int, y: Int) {
        if (sprite.hasAlpha) {
            imageRequests += ImageRequest(sprite, x, y, zDepth)
            
            return
        }
        
        if (x < -sprite.width) return
        if (y < -sprite.height) return
        if (x >= width) return
        if (y >= height) return
        
        var newX = 0
        var newY = 0
        var newW = sprite.width
        var newH = sprite.height
        
        if (x < 0) newX -= x
        if (y < 0) newY -= y
        if (x + newW > width) newW -= (newW + x - width)
        if (y + newH > height) newH -= (newH + y - height)
        
        for (py in newY until newH) {
            for (px in newX until newW) {
                setPixel(px + x, py + y, sprite.raster[px + py * sprite.width], sprite.lightBlock)
            }
        }
    }
    
    fun drawSprite(spriteSheet: SpriteSheet, x: Int, y: Int, sheetX: Int, sheetY: Int) {
        if (spriteSheet.hasAlpha) {
            imageRequests += ImageRequest(spriteSheet.getTileImage(sheetX, sheetY), x, y, zDepth)
            
            return
        }
        
        drawSprite(spriteSheet.getTileImage(sheetX, sheetY), x, y)
    }
    
    fun drawLight(light: Light, x: Int, y: Int) {
        lightRequests += LightRequest(light, x, y)
    }
    
    private fun drawLightRequest(lightRequest: LightRequest) {
        val (light, x, y) = lightRequest
        
        for (i in 0..light.diameter) {
            drawLightLine(light, i, 0, x, y)
            drawLightLine(light, i, light.diameter, x, y)
            drawLightLine(light, 0, i, x, y)
            drawLightLine(light, light.diameter, i, x, y)
        }
    }
    
    private fun drawLightLine(light: Light, x1: Int, y1: Int, offX: Int, offY: Int) {
        val x0 = light.radius
        val y0 = light.radius
        
        var x = x0
        var y = y0
        
        val dx = abs(x1 - x0)
        val dy = abs(y1 - y0)
        
        val sx = if (x0 < x1) 1 else -1
        val sy = if (y0 < y1) 1 else -1
        
        var err = dx - dy
        
        var strength = 1.0
        
        while (true) {
            val screenX = x - light.radius + offX
            val screenY = y - light.radius + offY
            
            if (screenX !in 0 until width || screenY !in 0 until height) return
            
            val lightColor = light[x, y]
            
            if (lightColor == 0) return
            
            val lightBlock = lightBlock[screenX + screenY * width]
            
            strength *= lightBlock
            
            if (strength == 0.0) return
            
            val color = Color(light[x, y]) * strength
            
            setLightMap(screenX, screenY, color.value)
            
            if (x == x1 && y == y1) return
            
            val e2 = 2 * err
            
            if (e2 > -1 * dy) {
                err -= dy
                x += sx
            }
            
            if (e2 < dx) {
                err += dx
                y += sy
            }
        }
    }
    
    private data class ImageRequest(val sprite: Sprite, val x: Int, val y: Int, val z: Int)
    
    private data class LightRequest(val light: Light, val x: Int, val y: Int)
}