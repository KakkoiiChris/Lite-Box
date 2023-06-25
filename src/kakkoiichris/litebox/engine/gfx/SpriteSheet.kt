package kakkoiichris.litebox.engine.gfx

class SpriteSheet(path: String, val tileWidth: Int, val tileHeight: Int) : Sprite(path) {
    fun getTileImage(tileX: Int, tileY: Int): Sprite {
        val raster = IntArray(tileWidth * tileHeight)
        
        for (y in 0 until tileHeight) {
            for (x in 0 until tileWidth) {
                raster[x + y * tileWidth] = this.raster[(x + tileX * tileWidth) + (y + tileY * tileHeight) * width]
            }
        }
        
        return Sprite(raster, tileWidth, tileHeight)
    }
}