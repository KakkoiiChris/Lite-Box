package kakkoiichris.litebox.engine

import java.util.*

/**
 * Lite-Box
 *
 * Copyright (C) 2023, KakkoiiChris
 *
 * File:    StateManager.kt
 *
 * Created: Sunday, June 25, 2023, 00:17:25
 *
 * @author Christian Bryce Alexander
 */
class StateManager {
    private val stack = Stack<State>()
    
    private var nextSwap: Swap? = null
    
    internal fun swap(display: Display) {
        val swap = nextSwap ?: return
        
        when (swap) {
            is Swap.Goto -> {
                if (stack.isNotEmpty()) {
                    stack.pop().leave(display)
                }
                
                stack.push(swap.state)
                
                stack.peek().enter(display)
            }
            
            is Swap.Push -> {
                if (stack.isNotEmpty()) {
                    stack.peek().leave(display)
                }
                
                stack.push(swap.state)
                
                stack.peek().enter(display)
            }
            
            Swap.Pop     -> {
                if (stack.isNotEmpty()) {
                    stack.peek().leave(display)
                }
                
                stack.pop()
                
                if (stack.isNotEmpty()) {
                    stack.peek().enter(display)
                }
            }
        }
        
        nextSwap = null
    }
    
    fun goto(state: State) {
        nextSwap = Swap.Goto(state)
    }
    
    fun push(state: State) {
        nextSwap = Swap.Push(state)
    }
    
    fun pop() {
        nextSwap = Swap.Pop
    }
    
    fun update(display: Display, delta: Double, input: Input) {
        if (stack.isNotEmpty()) {
            stack.peek().update(display, delta, input)
        }
    }
    
    fun render(display: Display, renderer: Renderer) {
        if (stack.isNotEmpty()) {
            stack.peek().render(display, renderer)
        }
    }
    
    private interface Swap {
        class Goto(val state: State) : Swap
        
        class Push(val state: State) : Swap
        
        object Pop : Swap
    }
}