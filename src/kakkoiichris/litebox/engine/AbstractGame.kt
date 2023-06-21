package kakkoiichris.litebox.engine

interface AbstractGame {
    fun update(display: Display, delta: Double, input: Input)
    
    fun render(display: Display, renderer: Renderer)
}