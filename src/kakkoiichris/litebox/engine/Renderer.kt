package kakkoiichris.litebox.engine

import kakkoiichris.litebox.engine.gfx.*
import java.awt.image.BufferedImage
import java.awt.image.DataBufferInt
import java.lang.Integer.max
import kotlin.math.abs

class Renderer(context: BufferedImage) {
    private val imageRequests = mutableListOf<ImageRequest>()
    private val lightRequests = mutableListOf<LightRequest>()
    
    private val width = context.width
    private val height = context.height
    private val raster = (context.raster.dataBuffer as DataBufferInt).data
    
    private val depthBuffer = IntArray(raster.size)
    private val lightMap = IntArray(raster.size)
    private val lightBlock = IntArray(raster.size)
    
    private val ambient = 0xFF123456.toInt()
    
    var font = Font.COMIC
    var zDepth = 0
    
    fun clear() {
        raster.fill(0)
        depthBuffer.fill(0)
        lightMap.fill(ambient)
    }
    
    fun process() {
        if (imageRequests.isNotEmpty()) {
            imageRequests.sortBy { it.z }
            
            var i = 0
            
            while (i < imageRequests.size) {
                val ir = imageRequests[i]
                
                zDepth = ir.z
                ir.image.hasAlpha = false
                drawImage(ir.image, ir.x, ir.y)
                ir.image.hasAlpha = true
                
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
        
        for (i in raster.indices) {
            val lr = ((lightMap[i] shr 16) and 0xFF) / 255f
            val lg = ((lightMap[i] shr 8) and 0xFF) / 255f
            val lb = (lightMap[i] and 0xFF) / 255f
            
            val pr = ((raster[i] shr 16) and 0xFF) / 255f
            val pg = ((raster[i] shr 8) and 0xFF) / 255f
            val pb = (raster[i] and 0xFF) / 255f
            
            val r = ((lr * pr) * 255).toInt()
            val g = ((lg * pg) * 255).toInt()
            val b = ((lb * pb) * 255).toInt()
            
            raster[i] = (r shl 16) or (g shl 8) or b
        }
    }
    
    fun setPixel(x: Int, y: Int, value: Int, block: Int) {
        val alpha = value shr 24 and 0xFF
        
        if (x !in 0 until width || y !in 0 until height || alpha == 0) return
        
        val index = x + y * width
        
        if (depthBuffer[index] > zDepth) return
        
        depthBuffer[index] = zDepth
        
        if (alpha == 255) {
            raster[index] = value
        }
        else {
            val pixelColor = raster[index]
            
            val newRed = (pixelColor shr 16 and 0xFF) - (((pixelColor shr 16 and 0xFF) - (value shr 16 and 0xFF)) * (alpha / 255.0)).toInt()
            val newGreen = (pixelColor shr 8 and 0xFF) - (((pixelColor shr 8 and 0xFF) - (value shr 8 and 0xFF)) * (alpha / 255.0)).toInt()
            val newBlue = (pixelColor and 0xFF) - (((pixelColor and 0xFF) - (value and 0xFF)) * (alpha / 255.0)).toInt()
            
            raster[index] = (0xFF shl 24) or (newRed shl 16) or (newGreen shl 8) or newBlue
        }
        
        lightBlock[index] = block
    }
    
    fun setLightMap(x: Int, y: Int, value: Int) {
        if (x !in 0 until width || y !in 0 until height) return
        
        val index = x + y * width
        
        val baseColor = lightMap[index]
        
        val maxRed = max((value shr 16) and 0xFF, (baseColor shr 16) and 0xFF)
        val maxGreen = max((value shr 8) and 0xFF, (baseColor shr 8) and 0xFF)
        val maxBlue = max(value and 0xFF, baseColor and 0xFF)
        
        lightMap[index] = (maxRed shl 16) or (maxGreen shl 8) or maxBlue
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
            
            if (screenX !in 0 until width || screenY !in 0 until height) return
            
            val lightColor = light[x, y]
            
            if (lightColor == 0) return
            
            if (lightBlock[screenX + screenY * width] == Light.FULL) return
            
            setLightMap(screenX, screenY, light[x, y])
            
            if (x == x1 && y == y1) break
            
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
    
    fun drawText(text: String, x: Int, y: Int, color: Int) {
        val fontImage = font.fontImage
        
        var offset = 0
        
        for (i in text.indices) {
            val unicode = text[i].code
            
            for (gy in 0 until fontImage.height) {
                for (gx in 0 until font.widths[unicode]) {
                    if (fontImage.raster[(gx + font.offsets[unicode]) + gy * fontImage.width] == 0xFFFFFFFF.toInt()) {
                        setPixel(gx + x + offset, gy + y, color, Light.NONE)
                    }
                }
            }
            
            offset += font.widths[unicode]
        }
    }
    
    fun drawImage(image: Image, x: Int, y: Int) {
        if (image.hasAlpha) {
            imageRequests += ImageRequest(image, x, y, zDepth)
            
            return
        }
        
        if (x < -image.width) return
        if (y < -image.height) return
        if (x >= width) return
        if (y >= height) return
        
        var newX = 0
        var newY = 0
        var newW = image.width
        var newH = image.height
        
        if (x < 0) newX -= x
        if (y < 0) newY -= y
        if (x + newW > width) newW -= (newW + x - width)
        if (y + newH > height) newH -= (newH + y - height)
        
        for (py in newY until newH) {
            for (px in newX until newW) {
                setPixel(px + x, py + y, image.raster[px + py * image.width], image.lightBlock)
            }
        }
    }
    
    fun drawImageTile(imageTile: ImageTile, x: Int, y: Int, tileX: Int, tileY: Int) {
        if (imageTile.hasAlpha) {
            imageRequests += ImageRequest(imageTile.getTileImage(tileX, tileY), x, y, zDepth)
            
            return
        }
        
        if (x < -imageTile.tileWidth) return
        if (y < -imageTile.tileHeight) return
        if (x >= width) return
        if (y >= height) return
        
        var newX = 0
        var newY = 0
        var newW = imageTile.tileWidth
        var newH = imageTile.tileHeight
        
        if (x < 0) newX -= x
        if (y < 0) newY -= y
        if (x + newW > width) newW -= (newW + x - width)
        if (y + newH > height) newH -= (newH + y - height)
        
        for (py in newY until newH) {
            for (px in newX until newW) {
                setPixel(px + x, py + y, imageTile.raster[(px + tileX * imageTile.tileWidth) + (py + tileY * imageTile.tileHeight) * imageTile.width], imageTile.lightBlock)
            }
        }
    }
    
    fun drawRect(x: Int, y: Int, width: Int, height: Int, color: Int) {
        for (py in 0 until height) {
            setPixel(x, py + y, color, Light.NONE)
            setPixel(x + width - 1, py + y, color, Light.NONE)
        }
        
        for (px in 0 until width) {
            setPixel(px + x, y, color, Light.NONE)
            setPixel(px + x, y + height - 1, color, Light.NONE)
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
                setPixel(px + x, py + y, color, Light.NONE)
            }
        }
    }
}