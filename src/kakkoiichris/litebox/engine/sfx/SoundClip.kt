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
        val audioSource = javaClass.getResourceAsStream(path)
        
        val bufferedIn = BufferedInputStream(audioSource)
        
        val ais = AudioSystem.getAudioInputStream(bufferedIn)
        
        val baseFormat = ais.format
        
        val decodeFormat = AudioFormat(
            PCM_SIGNED,
            baseFormat.sampleRate,
            16,
            baseFormat.channels,
            baseFormat.channels * 2,
            baseFormat.sampleRate,
            false
        )
        
        val dais = AudioSystem.getAudioInputStream(decodeFormat, ais)
        
        clip = AudioSystem.getClip()
        clip.open(dais)
        gain = clip.getControl(MASTER_GAIN) as FloatControl
    }
    
    fun play() {
        stop()
        clip.framePosition = 0
        clip.start()
    }
    
    fun loop() {
        stop()
        clip.framePosition = 0
        clip.loop(Clip.LOOP_CONTINUOUSLY)
    }
    
    fun stop() {
        clip.stop()
    }
    
    fun close() {
        stop()
        clip.drain()
        clip.close()
    }
}