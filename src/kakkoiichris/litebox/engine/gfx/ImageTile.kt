package kakkoiichris.litebox.engine.gfx

class ImageTile(path: String, val tileW: Int, val tileH: Int) : Image(path) {
    fun getTileImage(tileX: Int, tileY: Int): Image {
        val p = IntArray(tileW * tileH)
        
        for (y in 0 until tileH) {
            for (x in 0 until tileW) {
                p[x + y * tileW] = this.p[(x + tileX * tileW) + (y + tileY * tileH) * w]
            }
        }
        
        return Image(p, tileW, tileH)
    }
}