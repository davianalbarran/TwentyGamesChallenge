package io.github.davianalbarran.screens

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.Screen
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.utils.ScreenUtils
import io.github.davianalbarran.TwentyGameChallenge
import io.github.davianalbarran.screens.gamescreens.WaterDropScreen

class MainMenuScreen(private val game: TwentyGameChallenge) : Screen {
    override fun show() {
    }

    override fun render(delta: Float) {
        ScreenUtils.clear(Color.BLACK)

        game.viewport!!.apply()
        game.batch!!.setProjectionMatrix(game.viewport!!.camera.combined)

        game.batch!!.begin()
        //draw text. Remember that x and y are in meters
        game.gameFont!!.draw(game.batch, "Welcome to Drop!!! ", 1f, 1.5f)
        game.gameFont!!.draw(game.batch, "Tap anywhere or press space to begin!", 1f, 1f)
        game.batch!!.end()

        if (Gdx.input.isTouched || Gdx.input.isKeyPressed(Input.Keys.SPACE)) {
            game.screen = WaterDropScreen(game)
            dispose()
        }
    }

    override fun resize(width: Int, height: Int) {
        game.viewport!!.update(width, height)
    }

    override fun pause() {
    }

    override fun resume() {
    }

    override fun hide() {
    }

    override fun dispose() {
    }
}
