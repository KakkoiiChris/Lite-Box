package kakkoiichris.litebox.engine

import kakkoiichris.litebox.engine.GameContainer
import kakkoiichris.litebox.engine.Renderer

abstract class AbstractGame {
    abstract fun update(gc: GameContainer, dt: Float)
    abstract fun render(gc: GameContainer, r: Renderer)
}