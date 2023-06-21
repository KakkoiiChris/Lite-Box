package kakkoiichris.litebox.engine

abstract class AbstractGame {
    abstract fun update(display: Display, delta: Double)
    
    abstract fun render(display: Display, renderer: Renderer)
}