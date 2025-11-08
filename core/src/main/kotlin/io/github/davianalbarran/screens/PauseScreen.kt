package io.github.davianalbarran.screens

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Screen
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.scenes.scene2d.ui.TextButton
import com.badlogic.gdx.scenes.scene2d.ui.VerticalGroup
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener
import com.badlogic.gdx.utils.viewport.ScreenViewport
import io.github.davianalbarran.TwentyGameChallenge
import io.github.davianalbarran.screens.gamescreens.TwentyGameScreen

class PauseScreen(
    val game: TwentyGameChallenge,
    val screenPausedFrom: TwentyGameScreen
) : Screen {
    private val stage: Stage = Stage(ScreenViewport())
    private val verticalGroup: VerticalGroup = VerticalGroup()

    private val skin = Skin(Gdx.files.internal("uiskin.json"))

    private val resumeButton = TextButton("Resume", skin)
    private val mainMenuButton = TextButton("Go To Main Menu", skin)

    init {
        Gdx.input.inputProcessor = stage
        verticalGroup.setFillParent(true)

        stage.addActor(verticalGroup)

        verticalGroup.center()
        verticalGroup.space(20f)

        resumeButton.addListener(object : ClickListener() {
            override fun clicked(event: InputEvent?, x: Float, y: Float) {
                game.screen = screenPausedFrom
                screenPausedFrom.resume()
                dispose()
            }
        })

        mainMenuButton.addListener(object : ClickListener() {
            override fun clicked(event: InputEvent?, x: Float, y: Float) {
                game.screen = MainMenuScreen(game)
                screenPausedFrom.dispose()
                dispose()
            }
        })

        verticalGroup.addActor(resumeButton)
        verticalGroup.addActor(mainMenuButton)
    }

    override fun show() {
    }

    override fun render(delta: Float) {
        Gdx.gl.glClearColor(0f, 0f, 0f, 1f)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)
        stage.act(delta)
        stage.draw()
    }

    override fun resize(width: Int, height: Int) {
        stage.viewport.update(width, height, true)
    }

    override fun hide() {
    }

    override fun dispose() {
        stage.dispose()
    }

    // Unused methods
    override fun pause() {
    }

    override fun resume() {
    }
}
