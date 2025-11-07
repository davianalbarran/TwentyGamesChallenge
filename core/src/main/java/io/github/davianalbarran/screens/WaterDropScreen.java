package io.github.davianalbarran.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ScreenUtils;
import io.github.davianalbarran.TwentyGameChallenge;

public class WaterDropScreen implements Screen {
    private final TwentyGameChallenge game; // todo: separate each game

    private Stage uiStage;

    private Texture background;
    private Texture dropletTexture;
    private Sound dropSound;
    private Sound missSound;
    private Music music;

    private Sprite bucket;
    private Array<Drop> dropSprites;

    private float timer = 0.0f;

    private Rectangle bucketRectangle;
    private Rectangle dropletRectangle;

    private Label scoreLabel;
    private int score = 0;

    private float shakeDuration = 0f;
    private final float shakeMagnitude = 0.05f;  // tune this — larger = more shake
    private float shakeTimer = 0f;
    private float shakeTintAlpha = 0f;

    private static class Drop {
        public float speed;
        public Sprite droplet;

        private int pointValue = 1;

        public Drop(Sprite droplet, float speed, boolean isSupercharged) {
            this.droplet = droplet;
            this.speed = speed;
            if (isSupercharged)
                pointValue = 2;
        }

        public int getPointValue() { return pointValue; }
    }


    public WaterDropScreen(final TwentyGameChallenge game) {
        this.game = game;

        background = new Texture("background.png");
        dropletTexture = new Texture("drop.png");

        bucket = new Sprite(new Texture("bucket.png"));
        bucket.setSize(1, 1);

        dropSprites = new Array<>();

        dropSound = Gdx.audio.newSound(Gdx.files.internal("drop.mp3"));
        missSound = Gdx.audio.newSound(Gdx.files.internal("miss.wav"));

        music = Gdx.audio.newMusic(Gdx.files.internal("music.mp3"));
        music.setLooping(true);
        music.setVolume(.5f);

        bucketRectangle = new Rectangle();
        dropletRectangle = new Rectangle();

        Label.LabelStyle style = new Label.LabelStyle(game.gameFont, Color.WHITE);
        scoreLabel = new Label("Score: 0", style);
        scoreLabel.setPosition(20, Gdx.graphics.getHeight() - 60); // top-left corner

        uiStage = new Stage();
        uiStage.addActor(scoreLabel);
    }

    @Override
    public void show() {
        music.play();
    }

    @Override
    public void render(float delta) {
        input();
        logic();
        applyShake(Gdx.graphics.getDeltaTime());
        draw();
    }

    @Override
    public void resize(int width, int height) {

    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {
        music.dispose();
        dropSound.dispose();
        missSound.dispose();
        dropletTexture.dispose();
        background.dispose();
    }

    /// UTILITY METHODS ///
    private void input() {
        float speed = 5f;
        float delta = Gdx.graphics.getDeltaTime();

        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
            bucket.translateX(speed * delta);
        }

        if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
            bucket.translateX(-speed * delta);
        }
    }

    private void logic() {
        float worldWidth = game.viewport.getWorldWidth();
        float worldHeight = game.viewport.getWorldHeight();

        float delta = Gdx.graphics.getDeltaTime();

        // Clamp x to values between 0 and worldWidth
        bucket.setX(MathUtils.clamp(bucket.getX(), 0, worldWidth - bucket.getWidth()));

        bucketRectangle.set(bucket.getX(), bucket.getY(), bucket.getWidth(), bucket.getHeight());

        for (int i = dropSprites.size - 1; i >= 0; i--) {
            Drop drop = dropSprites.get(i);
            Sprite droplet = drop.droplet;

            float dropletWidth = droplet.getWidth();
            float dropletHeight = droplet.getHeight();

            dropletRectangle.set(droplet.getX(), droplet.getY(), dropletWidth, dropletHeight);

            droplet.translateY(-drop.speed * delta);

            if (droplet.getY() < -dropletHeight) {
                score--;
                dropSprites.removeIndex(i);
                startShake(0.25f);
                missSound.play(1f);
            } else if (bucketRectangle.overlaps(dropletRectangle)) {
                score += drop.getPointValue();
                dropSprites.removeIndex(i);
                dropSound.play();
            }
            scoreLabel.setText("Score: " + score);
        }

        timer += delta;

        if (timer >= 1f) {
            timer = 0;
            createDroplet();
        }
    }

    private void draw() {
        ScreenUtils.clear(0.15f, 0.15f, 0.2f, 1f);
        game.viewport.apply();
        game.batch.setProjectionMatrix(game.viewport.getCamera().combined);

        game.batch.begin();

        float worldWidth = game.viewport.getWorldWidth();
        float worldHeight = game.viewport.getWorldHeight();

        game.batch.draw(background, 0, 0, worldWidth, worldHeight);
        bucket.draw(game.batch);

        for (Drop drop : dropSprites)
            drop.droplet.draw(game.batch);

        if (shakeTintAlpha > 0f) {
            game.batch.setColor(1f, 0f, 0f, shakeTintAlpha);
            game.batch.draw(background, 0, 0, game.viewport.getWorldWidth(), game.viewport.getWorldHeight());
            game.batch.setColor(Color.WHITE);
        }

        scoreLabel.draw(game.batch, 1f);

        game.batch.end();

        uiStage.act(Gdx.graphics.getDeltaTime());
        uiStage.draw();
    }

    private void createDroplet() {
        float dropWidth = 1;
        float dropHeight = 1;
        float worldWidth = game.viewport.getWorldWidth();
        float worldHeight = game.viewport.getWorldHeight();

        Sprite dropSprite = new Sprite(dropletTexture);
        dropSprite.setSize(dropWidth, dropHeight);
        dropSprite.setX(MathUtils.random(0f, worldWidth - dropWidth));
        dropSprite.setY(worldHeight);
        boolean isSupercharged = MathUtils.random(1, 6) == 1;

        if (isSupercharged)
            dropSprite.setColor(Color.GREEN);

        Drop drop = new Drop(dropSprite, MathUtils.random(1, 5), isSupercharged);
        dropSprites.add(drop);
    }

    private void startShake(float duration) {
        shakeDuration = duration;
        shakeTimer = duration;
    }

    private void applyShake(float delta) {
        if (shakeTimer > 0) {
            shakeTimer -= delta;

            float decay = shakeTimer / shakeDuration;
            float currentPower = shakeMagnitude * decay;
            shakeTintAlpha = 0.2f * decay;

            float offsetX = (MathUtils.random() - 0.5f) * 2 * currentPower;
            float offsetY = (MathUtils.random() - 0.5f) * 2 * currentPower;

            game.viewport.getCamera().position.x = game.viewport.getWorldWidth() / 2 + offsetX;
            game.viewport.getCamera().position.y = game.viewport.getWorldHeight() / 2 + offsetY;
            game.viewport.getCamera().update();
        } else {
            shakeTintAlpha = 0f;
            game.viewport.getCamera().position.set(game.viewport.getWorldWidth() / 2, game.viewport.getWorldHeight() / 2, 0);
            game.viewport.getCamera().update();
        }
    }
}
