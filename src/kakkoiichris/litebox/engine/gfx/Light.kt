package kakkoiichris.litebox.engine.gfx

import kotlin.math.sqrt

class Light(val radius: Int, val color: Int) {
    companion object {
        const val NONE = 0
        const val FULL = 1
    }
    
    val diameter = radius * 2
    
    val lm = IntArray(diameter * diameter) { i ->
        val x = (i % diameter) - radius
        val y = (i / diameter) - radius
        
        val distance = sqrt((x * x + y * y).toDouble())
        
        if (distance >= radius) return@IntArray 0
        
        val power = 1.0 - (distance / radius)
        
        val cr = ((color shr 16) and 0xFF) / 255f
        val cg = ((color shr 8) and 0xFF) / 255f
        val cb = (color and 0xFF) / 255f
        
        val r = ((cr * power) * 255).toInt()
        val g = ((cg * power) * 255).toInt()
        val b = ((cb * power) * 255).toInt()
        
        (r shl 16) or (g shl 8) or b
    }
    
    operator fun get(x: Int, y: Int): Int {
        if (x !in 0 until diameter || y !in 0 until diameter) {
            return 0
        }
        
        return lm[x + y * diameter]
    }
}