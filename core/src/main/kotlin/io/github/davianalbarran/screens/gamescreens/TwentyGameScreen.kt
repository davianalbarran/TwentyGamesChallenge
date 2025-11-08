package io.github.davianalbarran.screens.gamescreens

import com.badlogic.gdx.Screen
import io.github.davianalbarran.TwentyGameChallenge

sealed class TwentyGameScreen(
    val game : TwentyGameChallenge
) : Screen {

    var isPaused: Boolean = false

    override fun render(delta: Float) {
        input()
        if (!isPaused) logic()
        draw()
    }

    override fun resize(width: Int, height: Int) {
        game.viewport!!.update(width, height);
    }

    override fun pause() {
        isPaused = true
        print("paused")
    }

    override fun resume() {
        isPaused = false
        print("resumed")
    }

    abstract override fun hide()
    abstract override fun show()
    abstract override fun dispose()

    ///// UTILITY METHODS /////
    abstract fun input()
    abstract fun logic()
    abstract fun draw()
}
