package kakkoiichris.litebox.engine.gfx

import kotlin.math.sqrt

class Light(val radius: Int, color: Int) {
    val diameter = radius * 2
    
    private val lightMap = IntArray(diameter * diameter) { i ->
        val x = (i % diameter) - radius
        val y = (i / diameter) - radius
        
        val distance = sqrt((x * x + y * y).toDouble())
        
        if (distance >= radius) return@IntArray 0
        
        val power = 1.0 - (distance / radius)
        
        val red = (((color shr 16) and 0xFF) * power).toInt()
        val green = (((color shr 8) and 0xFF) * power).toInt()
        val blue = ((color and 0xFF) * power).toInt()
        
        (red shl 16) or (green shl 8) or blue
    }
    
    operator fun get(x: Int, y: Int): Int {
        if (x !in 0 until diameter || y !in 0 until diameter) {
            return 0
        }
        
        return lightMap[x + y * diameter]
    }
}