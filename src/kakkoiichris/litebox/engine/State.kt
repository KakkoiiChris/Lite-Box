package kakkoiichris.litebox.engine

interface State {
    fun enter(display: Display)
    
    fun leave(display: Display)
    
    fun update(display: Display, delta: Double, input: Input)
    
    fun render(display: Display, renderer: Renderer)
}