package kakkoiichris.litebox.engine

abstract class AbstractGame {
    abstract fun update(container: GameContainer, delta: Float)
    
    abstract fun render(container: GameContainer, renderer: Renderer)
}