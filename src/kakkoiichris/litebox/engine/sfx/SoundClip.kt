package kakkoiichris.litebox.engine.sfx

import java.io.BufferedInputStream
import javax.sound.sampled.AudioFormat
import javax.sound.sampled.AudioFormat.Encoding.PCM_SIGNED
import javax.sound.sampled.AudioSystem
import javax.sound.sampled.Clip
import javax.sound.sampled.FloatControl
import javax.sound.sampled.FloatControl.Type.MASTER_GAIN

/**
 * ClassDescription
 *
 * @author Christian Bryce Alexander
 * @since 7/20/2017, 9:54 PM
 */
class SoundClip(path: String) {
    private val clip: Clip
    private val gain: FloatControl
    
    var volume: Float
        get() = gain.value
        set(value) {
            gain.value = value
        }
    
    init {
        val source = javaClass.getResourceAsStream(path) ?: throw IllegalArgumentException("No sound file found at path '$path'!")
        
        val buffer = BufferedInputStream(source)
        
        val stream = AudioSystem.getAudioInputStream(buffer)
        
        val baseFormat = stream.format
        
        val decodedFormat = AudioFormat(
            PCM_SIGNED,
            baseFormat.sampleRate,
            16,
            baseFormat.channels,
            baseFormat.channels * 2,
            baseFormat.sampleRate,
            false
        )
        
        val decodedStream = AudioSystem.getAudioInputStream(decodedFormat, stream)
        
        clip = AudioSystem.getClip()
        
        clip.open(decodedStream)
        
        gain = clip.getControl(MASTER_GAIN) as FloatControl
    }
    
    fun play() = with(clip) {
        stop()
        
        framePosition = 0
        
        start()
    }
    
    fun loop() = with(clip) {
        stop()
        
        framePosition = 0
        
        loop(Clip.LOOP_CONTINUOUSLY)
    }
    
    fun stop() = clip.stop()
    
    fun close() = with(clip) {
        stop()
        drain()
        close()
    }
}