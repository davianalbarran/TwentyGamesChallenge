package io.github.davianalbarran;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.viewport.FitViewport;
import io.github.davianalbarran.screens.MainMenuScreen;

public class TwentyGameChallenge extends Game {
    private MainMenuScreen mainMenuScreen;

    public SpriteBatch batch;
    public FitViewport viewport;
    public BitmapFont gameFont;

    @Override
    public void create() {
        batch = new SpriteBatch();

        gameFont = new BitmapFont();
        viewport = new FitViewport(8, 5);

        gameFont.setUseIntegerPositions(false);
        gameFont.getData().setScale(viewport.getWorldHeight() / Gdx.graphics.getHeight());

        mainMenuScreen = new MainMenuScreen(this);

        this.setScreen(mainMenuScreen);
    }

    public void render() {
        super.render();
    }

    public void dispose() {
        batch.dispose();
        gameFont.dispose();
        mainMenuScreen.dispose();
    }
}
