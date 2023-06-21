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
    val alpha: Int get() = (value shr 24) and 0xFF
    
    val red: Int get() = (value shr 16) and 0xFF
    
    val green: Int get() = (value shr 8) and 0xFF
    
    val blue: Int get() = value and 0xFF
    
    constructor(alpha: Int, red: Int, green: Int, blue: Int) : this((alpha shl 24) or (red shl 16) or (green shl 8) or blue)
    
    fun blend(other: Color): Color {
        val newRed = (red - ((red - other.red) * (other.alpha / 255.0))).toInt()
        val newGreen = (green - ((green - other.green) * (other.alpha / 255.0))).toInt()
        val newBlue = (blue - ((blue - other.blue) * (other.alpha / 255.0))).toInt()
        
        return Color(0xFF, newRed, newGreen, newBlue)
    }
    
    fun max(other: Color): Color {
        val newRed = kotlin.math.max(red, other.red)
        val newGreen = kotlin.math.max(green, other.green)
        val newBlue = kotlin.math.max(blue, other.blue)
        
        return Color(alpha, newRed, newGreen, newBlue)
    }
    
    operator fun times(scale: Double): Color {
        val scaleRed = (red * scale).toInt()
        val scaleGreen = (green * scale).toInt()
        val scaleBlue = (blue * scale).toInt()
        
        return Color(alpha, scaleRed, scaleGreen, scaleBlue)
    }
    
    operator fun times(other: Color): Color {
        val scaleRed = (red * (other.red / 255.0)).toInt()
        val scaleGreen = (green * (other.green / 255.0)).toInt()
        val scaleBlue = (blue * (other.blue / 255.0)).toInt()
        
        return Color(alpha, scaleRed, scaleGreen, scaleBlue)
    }
}