package io.github.davianalbarran.screens.gamescreens

import com.badlogic.gdx.Screen
import io.github.davianalbarran.TwentyGameChallenge
import io.github.davianalbarran.screens.PauseScreen

sealed class TwentyGameScreen(
    val game : TwentyGameChallenge
) : Screen {

    var isPaused: Boolean = false

    override fun render(delta: Float) {
        input(delta)
        if (!isPaused) logic(delta)
        draw(delta)
    }

    override fun resize(width: Int, height: Int) {
        game.viewport!!.update(width, height);
    }

    override fun pause() {
        isPaused = true
        game.screen = PauseScreen(game, this)
    }

    override fun resume() {
        isPaused = false
    }

    abstract override fun hide()
    abstract override fun show()
    abstract override fun dispose()

    ///// UTILITY METHODS /////
    abstract fun input(delta: Float)
    abstract fun logic(delta: Float)
    abstract fun draw(delta: Float)
}
