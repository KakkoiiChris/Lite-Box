package kakkoiichris.litebox.engine.gfx

class ImageTile(path: String, val tileWidth: Int, val tileHeight: Int) : Image(path) {
    fun getTileImage(tileX: Int, tileY: Int): Image {
        val raster = IntArray(tileWidth * tileHeight)
        
        for (y in 0 until tileHeight) {
            for (x in 0 until tileWidth) {
                raster[x + y * tileWidth] = this.raster[(x + tileX * tileWidth) + (y + tileY * tileHeight) * width]
            }
        }
        
        return Image(raster, tileWidth, tileHeight)
    }
}