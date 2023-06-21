package kakkoiichris.litebox.engine.gfx

/**
 * Lite-Box
 *
 * Copyright (C) 2023, KakkoiiChris
 *
 * File:    Color.kt
 *
 * Created: Tuesday, June 20, 2023, 23:13:27
 *
 * @author Christian Bryce Alexander
 */
@JvmInline
value class Color(val value: Int) {
    val red: Int get() = (value shr 16) and 0xFF
    
    val green: Int get() = (value shr 8) and 0xFF
    
    val blue: Int get() = value and 0xFF
    
    operator fun times(scale: Double): Color {
        val scaleRed = (red * scale).toInt()
        val scaleGreen = (green * scale).toInt()
        val scaleBlue = (blue * scale).toInt()
        
        return Color((scaleRed shl 16) or (scaleGreen shl 8) or scaleBlue)
    }
}