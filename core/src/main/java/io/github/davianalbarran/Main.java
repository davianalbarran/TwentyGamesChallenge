package io.github.davianalbarran;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class Main extends ApplicationAdapter {
    private SpriteBatch batch;
    private FitViewport viewport;
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

    private BitmapFont scoreFont;
    private Label scoreLabel;
    private int score = 0;

    private float shakeDuration = 0f;
    private final float shakeMagnitude = 0.05f;  // tune this — larger = more shake
    private float shakeTimer = 0f;
    private float shakeTintAlpha = 0f;

    private class Drop {
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

    @Override
    public void create() {
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
        music.play();

        batch = new SpriteBatch();
        viewport = new FitViewport(8, 5);
        uiStage = new Stage(new ScreenViewport()); // for UI elements

        bucketRectangle = new Rectangle();
        dropletRectangle = new Rectangle();

        BitmapFont scoreFont = new BitmapFont();

        Label.LabelStyle style = new Label.LabelStyle(scoreFont, Color.WHITE);
        scoreLabel = new Label("Score: 0", style);
        scoreLabel.setPosition(20, Gdx.graphics.getHeight() - 60); // top-left corner

        uiStage.addActor(scoreLabel);
    }

    @Override
    public void render() {
        input();
        logic();
        applyShake(Gdx.graphics.getDeltaTime());
        draw();
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true);
    }

    @Override
    public void dispose() {
        background.dispose();
        dropletTexture.dispose();
        dropSound.dispose();
        music.dispose();
        uiStage.dispose();
    }

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
        float worldWidth = viewport.getWorldWidth();
        float worldHeight = viewport.getWorldHeight();

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
        viewport.apply();
        batch.setProjectionMatrix(viewport.getCamera().combined);

        batch.begin();

        float worldWidth = viewport.getWorldWidth();
        float worldHeight = viewport.getWorldHeight();

        batch.draw(background, 0, 0, worldWidth, worldHeight);
        bucket.draw(batch);

        for (Drop drop : dropSprites)
            drop.droplet.draw(batch);

        if (shakeTintAlpha > 0f) {
            batch.setColor(1f, 0f, 0f, shakeTintAlpha);
            batch.draw(background, 0, 0, viewport.getWorldWidth(), viewport.getWorldHeight());
            batch.setColor(Color.WHITE);
        }

        scoreLabel.draw(batch, 1f);

        batch.end();

        uiStage.act(Gdx.graphics.getDeltaTime());
        uiStage.draw();
    }

    private void createDroplet() {
        float dropWidth = 1;
        float dropHeight = 1;
        float worldWidth = viewport.getWorldWidth();
        float worldHeight = viewport.getWorldHeight();

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

            viewport.getCamera().position.x = viewport.getWorldWidth() / 2 + offsetX;
            viewport.getCamera().position.y = viewport.getWorldHeight() / 2 + offsetY;
            viewport.getCamera().update();
        } else {
            shakeTintAlpha = 0f;
            viewport.getCamera().position.set(viewport.getWorldWidth() / 2, viewport.getWorldHeight() / 2, 0);
            viewport.getCamera().update();
        }
    }
}
