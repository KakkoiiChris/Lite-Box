package kakkoiichris.litebox.engine

class GameContainer(private val game: AbstractGame) : Runnable {
    val width = 320
    val height = 240
    val scale = 2F
    val title = "MajEngine v1.0"
    
    val window = Window(this)
    private val renderer = Renderer(this)
    val input = Input(this)
    
    private val thread = Thread(this)
    private var running = false
    private val updateCap = 1.0 / 60.0
    
    fun start() {
        thread.start()
    }
    
    fun stop() {
    
    }
    
    override fun run() {
        running = true
        
        var render: Boolean
        
        var firstTime: Double
        var lastTime = System.nanoTime() / 1.0e9
        var passedTime: Double
        var unprocessedTime = 0.0
        
        var frameTime = 0.0
        
        var frames = 0
        var fps = 0
        
        while (running) {
            render = false
            
            firstTime = System.nanoTime() / 1.0e9
            passedTime = firstTime - lastTime
            lastTime = firstTime
            
            unprocessedTime += passedTime
            frameTime += passedTime
            
            while (unprocessedTime >= updateCap) {
                unprocessedTime -= updateCap
                render = true
                
                game.update(this, updateCap.toFloat())
                
                input.update()
                
                if (frameTime >= 1) {
                    frameTime = 0.0
                    fps = frames
                    frames = 0
                }
            }
            
            if (render) {
                renderer.clear()
                game.render(this, renderer)
                renderer.process()
                renderer.drawText("FPS: $fps", 0, 0, 0xFF00FFFF.toInt())
                window.update()
                frames++
            }
            else {
                Thread.sleep(1)
            }
        }
        
        dispose()
    }
    
    private fun dispose() {
    
    }
}