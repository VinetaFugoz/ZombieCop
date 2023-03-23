import SidesEnum.*
import entities.GameStateEnum
import entities.GameStateEnum.*
import entities.Player
import graphics.SpriteSheet
import graphics.UI
import world.Camera
import world.World
import world.World.Companion.player
import java.awt.*
import java.awt.event.KeyEvent
import java.awt.event.KeyEvent.*
import java.awt.event.KeyListener
import java.awt.image.BufferStrategy
import java.awt.image.BufferedImage
import java.lang.System.nanoTime
import java.util.*
import javax.swing.JFrame
import javax.swing.JFrame.EXIT_ON_CLOSE
import kotlin.system.exitProcess

class Game : Canvas(), Runnable, KeyListener {

    private val gameThread: Thread = Thread(this)
    private var shouldKeepRunning: Boolean = false
    private val gameImage: BufferedImage
    private val ui: UI
    private var blinkAnimation: BlinkAnimation

    companion object {
        private const val GAME_TITLE = "Graphics"

        var random: Random = Random()
        var goNextLevel: Boolean = false
        var current_level = 1
        var gameState: GameStateEnum = NORMAL

        const val WIDTH: Int = 160
        const val HEIGHT: Int = 120
        private const val SCALE: Int = 5

        const val MAX_LEVEL = 2

        private const val TARGET_FPS: Int = 60
        private const val ONE_SECOND_IN_NANOSECONDS: Long = 1_000_000_000L
        private const val TIME_PER_FRAME: Long = ONE_SECOND_IN_NANOSECONDS / TARGET_FPS

        val SPRITE_SHEET = SpriteSheet("/sprite_sheet.png")

        var WORLD: World = World("/level$current_level.png")
    }

    init {
        preferredSize = Dimension(WIDTH * SCALE, HEIGHT * SCALE)

        gameImage = BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_ARGB)

        JFrame(GAME_TITLE).run {
            add(this@Game)
            pack()
            isVisible = true
            isResizable = false
            setLocationRelativeTo(null)
            defaultCloseOperation = EXIT_ON_CLOSE
        }

        ui = UI()

        createBufferStrategy(3)
        addKeyListener(this)
        blinkAnimation = BlinkAnimation(5)
    }

    @Synchronized
    fun start() {
        shouldKeepRunning = true

        gameThread.start()
    }

    @Synchronized
    fun stop() {
        exitProcess(0)
    }

    override fun run() = gameLoop()

    private fun gameLoop() {
        var startNanoTime: Long = nanoTime()
        var lastFrameNanoTime: Long = startNanoTime
        var framesPerSecond = 0

        try {
            while (shouldKeepRunning) {
                val currentNanoTime = nanoTime()
                if (hasPassedAFrame(currentNanoTime, lastFrameNanoTime)) {
                    updateAndDrawGame()

                    framesPerSecond++
                    lastFrameNanoTime = nanoTime()
                }

                if (hasPassedASecond(currentNanoTime, startNanoTime)) {
                    printFrameCount(framesPerSecond)

                    framesPerSecond = 0
                    startNanoTime = currentNanoTime
                }
            }
            stop()
        } catch (exception: Exception) {
            exception.printStackTrace()
        }
    }

    private fun hasPassedAFrame(now: Long, lastFrameTime: Long) = now - lastFrameTime >= TIME_PER_FRAME

    private fun hasPassedASecond(now: Long, startTime: Long) = now - startTime >= ONE_SECOND_IN_NANOSECONDS

    private fun printFrameCount(frameCount: Int) = println("FPS: $frameCount")

    private fun updateAndDrawGame() {
        updateGame()
        drawGame()
    }

    private fun updateGame() {

        WORLD.update()
        Camera.update()

        if (goNextLevel) {
            if (current_level < MAX_LEVEL) {
                current_level++
                WORLD.level = "/level$current_level.png"
                WORLD.initWorld()
            } else {
                gameState = CONGRATULATIONS
            }

            goNextLevel = false
        }
    }

    private fun drawGame() {
        val bufferStrategy: BufferStrategy = bufferStrategy

        gameImage.graphics.run {
            color = Color.BLACK
            fillRect(0, 0, WIDTH, HEIGHT)
            val enterMessage = "Press <Enter> to continue!"
            val graphics2D: Graphics2D = this as Graphics2D

            when (gameState) {
                NORMAL -> {
                    WORLD.draw(this)
                    ui.draw(graphics2D)
                }

                CONGRATULATIONS -> {
                    WORLD.draw(this)
                    ui.draw(this)
                    color = Color.BLUE
                    fillRect(0, 0, WIDTH, HEIGHT)
                    color = Color.WHITE
                    font = Font("arial", Font.BOLD, 14)
                    drawString(CONGRATULATIONS.getText(), WIDTH / CONGRATULATIONS.getText().length, HEIGHT / 2)
                    font = Font("arial", Font.BOLD, 10)
                    if (!blinkAnimation.animate()) drawString(enterMessage, WIDTH / enterMessage.length, HEIGHT / 2 + 14)
                }

                GAME_OVER -> {
                    WORLD.draw(this)
                    ui.draw(this)
                    graphics2D.apply {
                        color = Color.BLACK
                        composite = (AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.8f))
                        fillRect(0, 0, WIDTH, HEIGHT)
                        color = Color.WHITE
                        font = Font("arial", Font.BOLD, 20)
                        drawString(GAME_OVER.getText(), WIDTH / GAME_OVER.getText().length, HEIGHT / 2)

                        font = Font("arial", Font.BOLD, 10)
                        if (!blinkAnimation.animate()) drawString(enterMessage, WIDTH / enterMessage.length * 2, HEIGHT / 2 + 20)
                        composite = (AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f))

                    }
                }
            }

            dispose()
        }

        val gameGraphics: Graphics = bufferStrategy.drawGraphics
        gameGraphics.drawImage(gameImage, 0, 0, WIDTH * SCALE, HEIGHT * SCALE, null)

        bufferStrategy.show()
    }

    override fun keyTyped(e: KeyEvent?) {
        // We don`t need this fun because we are not treating typed events in this game
    }

    override fun keyPressed(event: KeyEvent) {
        when (event.keyCode) {
            VK_UP, VK_W -> {
                player.goUp = true; player.lastSide = UP.ordinal
            }

            VK_DOWN, VK_S -> {
                player.goDown = true; player.lastSide = DOWN.ordinal
            }
        }

        when (event.keyCode) {
            VK_LEFT, VK_A -> {
                player.goLeft = true; player.lastSide = LEFT.ordinal
            }

            VK_RIGHT, VK_D -> {
                player.goRight = true; player.lastSide = RIGHT.ordinal
            }
        }

        if (event.keyCode == VK_SPACE) player.shoot = true

        if (event.keyCode == VK_ENTER && (gameState == GAME_OVER || gameState == CONGRATULATIONS)) {
            current_level = 1
            WORLD = World("/level$current_level.png")
            gameState = NORMAL
            Player.resetPlayer()
        }
    }

    override fun keyReleased(event: KeyEvent) {
        when (event.keyCode) {
            VK_UP, VK_W -> player.goUp = false
            VK_DOWN, VK_S -> player.goDown = false
        }

        when (event.keyCode) {
            VK_LEFT, VK_A -> player.goLeft = false
            VK_RIGHT, VK_D -> player.goRight = false
        }

        if (event.keyCode == VK_SPACE) player.shoot = false
    }
}