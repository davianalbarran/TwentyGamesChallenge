package io.github.davianalbarran.screens.gamescreens

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.audio.Music
import com.badlogic.gdx.audio.Sound
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.utils.Array
import com.badlogic.gdx.utils.ScreenUtils
import io.github.davianalbarran.TwentyGameChallenge

class WaterDropScreen(// todo: separate each game
    lGame : TwentyGameChallenge
) : TwentyGameScreen(lGame) {
    private val background: Texture = Texture("background.png")
    private val dropletTexture: Texture = Texture("drop.png")
    private val dropSound: Sound
    private val missSound: Sound
    private val music: Music

    private val bucket: Sprite = Sprite(Texture("bucket.png"))
    private val dropSprites: Array<Drop>

    private var timer = 0.0f

    private val bucketRectangle: Rectangle
    private val dropletRectangle: Rectangle

    private var score = 0

    private var shakeDuration = 0f
    private val shakeMagnitude = 0.05f // tune this — larger = more shake
    private var shakeTimer = 0f
    private var shakeTintAlpha = 0f

    private class Drop(var droplet: Sprite, var speed: Float, isSupercharged: Boolean) {
        var pointValue: Int = 1
            private set

        init {
            if (isSupercharged) pointValue = 2
        }
    }


    init {
        bucket.setSize(1f, 1f)

        dropSprites = Array<Drop>()

        dropSound = Gdx.audio.newSound(Gdx.files.internal("drop.mp3"))
        missSound = Gdx.audio.newSound(Gdx.files.internal("miss.wav"))

        music = Gdx.audio.newMusic(Gdx.files.internal("music.mp3"))
        music.isLooping = true
        music.volume = .5f

        bucketRectangle = Rectangle()
        dropletRectangle = Rectangle()
    }

    override fun show() {
        music.play()
    }
    override fun hide() {
    }

    override fun dispose() {
        music.dispose()
        dropSound.dispose()
        missSound.dispose()
        dropletTexture.dispose()
        background.dispose()
    }

    //////// UTILITY METHODS ////////
    override fun input() {
        val speed = 5f
        val delta = Gdx.graphics.deltaTime

        if (!isPaused && Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
            bucket.translateX(speed * delta)
        }

        if (!isPaused && Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
            bucket.translateX(-speed * delta)
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE))
            if (isPaused) resume() else pause()
    }

    override fun logic() {
        val worldWidth = game.viewport!!.worldWidth

        val delta = Gdx.graphics.deltaTime

        // Clamp x to values between 0 and worldWidth
        bucket.setX(MathUtils.clamp(bucket.x, 0f, worldWidth - bucket.getWidth()))

        bucketRectangle.set(bucket.x, bucket.y, bucket.getWidth(), bucket.getHeight())

        for (i in dropSprites.size - 1 downTo 0) {
            val drop = dropSprites.get(i)
            val droplet = drop.droplet

            val dropletWidth = droplet.getWidth()
            val dropletHeight = droplet.getHeight()

            dropletRectangle.set(droplet.x, droplet.y, dropletWidth, dropletHeight)

            droplet.translateY(-drop.speed * delta)

            if (droplet.y < -dropletHeight) {
                score--
                dropSprites.removeIndex(i)
                startShake(0.25f)
                missSound.play(1f)
            } else if (bucketRectangle.overlaps(dropletRectangle)) {
                score += drop.pointValue
                dropSprites.removeIndex(i)
                dropSound.play()
            }
        }

        timer += delta

        if (timer >= 1f) {
            timer = 0f
            createDroplet()
        }
    }

    override fun draw() {
        applyShake(Gdx.graphics.deltaTime)
        ScreenUtils.clear(0.15f, 0.15f, 0.2f, 1f)
        game.viewport!!.apply()
        game.batch!!.setProjectionMatrix(game.viewport!!.camera.combined)

        game.batch!!.begin()

        val worldWidth = game.viewport!!.worldWidth
        val worldHeight = game.viewport!!.worldHeight

        game.batch!!.draw(background, 0f, 0f, worldWidth, worldHeight)
        bucket.draw(game.batch)

        for (drop in dropSprites) drop.droplet.draw(game.batch)

        game.gameFont!!.draw(game.batch, "Score: $score", 0f, worldHeight)

        if (shakeTintAlpha > 0f) {
            game.batch!!.setColor(1f, 0f, 0f, shakeTintAlpha)
            game.batch!!.draw(background, 0f, 0f, game.viewport!!.worldWidth, game.viewport!!.worldHeight)
            game.batch!!.setColor(Color.WHITE)
        }

        game.batch!!.end()
    }

    private fun createDroplet() {
        val dropWidth = 1f
        val dropHeight = 1f
        val worldWidth = game.viewport!!.worldWidth
        val worldHeight = game.viewport!!.worldHeight

        val dropSprite = Sprite(dropletTexture)
        dropSprite.setSize(dropWidth, dropHeight)
        dropSprite.setX(MathUtils.random(0f, worldWidth - dropWidth))
        dropSprite.setY(worldHeight)
        val isSupercharged = MathUtils.random(1, 6) == 1

        if (isSupercharged) dropSprite.setColor(Color.GREEN)

        val drop = Drop(dropSprite, MathUtils.random(1, 5).toFloat(), isSupercharged)
        dropSprites.add(drop)
    }

    private fun startShake(duration: Float) {
        shakeDuration = duration
        shakeTimer = duration
    }

    private fun applyShake(delta: Float) {
        if (shakeTimer > 0) {
            shakeTimer -= delta

            val decay = shakeTimer / shakeDuration
            val currentPower = shakeMagnitude * decay
            shakeTintAlpha = 0.2f * decay

            val offsetX = (MathUtils.random() - 0.5f) * 2 * currentPower
            val offsetY = (MathUtils.random() - 0.5f) * 2 * currentPower

            game.viewport!!.camera.position.x = game.viewport!!.worldWidth / 2 + offsetX
            game.viewport!!.camera.position.y = game.viewport!!.worldHeight / 2 + offsetY
            game.viewport!!.camera.update()
        } else {
            shakeTintAlpha = 0f
            game.viewport!!.camera.position.set(
                game.viewport!!.worldWidth / 2,
                game.viewport!!.worldHeight / 2,
                0f
            )
            game.viewport!!.camera.update()
        }
    }
}
