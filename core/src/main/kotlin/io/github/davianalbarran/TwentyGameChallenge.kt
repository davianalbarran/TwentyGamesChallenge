package io.github.davianalbarran

import com.badlogic.gdx.Game
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.utils.viewport.FitViewport
import io.github.davianalbarran.screens.MainMenuScreen

class TwentyGameChallenge : Game() {
    private var mainMenuScreen: MainMenuScreen? = null

    @JvmField
    var batch: SpriteBatch? = null
    @JvmField
    var viewport: FitViewport? = null
    @JvmField
    var gameFont: BitmapFont? = null

    override fun create() {
        batch = SpriteBatch()

        gameFont = BitmapFont()
        viewport = FitViewport(8f, 5f)

        gameFont!!.setUseIntegerPositions(false)
        gameFont!!.getData().setScale(viewport!!.getWorldHeight() / Gdx.graphics.getHeight())

        mainMenuScreen = MainMenuScreen(this)

        this.setScreen(mainMenuScreen)
    }

    override fun render() {
        super.render()
    }

    override fun dispose() {
        batch!!.dispose()
        gameFont!!.dispose()
        mainMenuScreen!!.dispose()
    }
}
