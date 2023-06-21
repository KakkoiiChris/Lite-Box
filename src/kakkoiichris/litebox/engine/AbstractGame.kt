package kakkoiichris.litebox.engine

abstract class AbstractGame {
    abstract fun update(gc: GameContainer, dt: Float)
    abstract fun render(gc: GameContainer, r: Renderer)
}