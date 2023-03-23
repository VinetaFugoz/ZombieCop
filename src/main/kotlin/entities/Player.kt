package entities

import BlinkAnimation
import MoveAnimation
import Game.Companion.SPRITE_SHEET
import Game.Companion.WORLD
import Game.Companion.gameState
import SidesEnum.*
import entities.GameStateEnum.GAME_OVER
import world.Camera
import world.World
import java.awt.Graphics
import java.awt.Rectangle
import java.awt.image.BufferedImage

class Player(sprite: BufferedImage, x: Int, y: Int) : Entity(sprite, x, y) {

    var goUp: Boolean = false
    var goDown: Boolean = false
    var goLeft: Boolean = false
    var goRight: Boolean = false
    var shoot: Boolean = false

    private var tookDamage: Boolean = false

    var lastSide: Int = DOWN.ordinal

    private val moveAnimation: MoveAnimation
    private var blinkAnimation: BlinkAnimation

    companion object {
        var life: kotlin.Double = 0.0
        var bullets: Int = 0
        var lives: Int = 1
        var hasGun: Boolean = false
        var tookLife: Boolean = false

        const val MAX_LIFE: kotlin.Double = 100.0
        const val MAX_BULLETS: Int = 25
        const val MAX_LIVES: Int = 3

        private fun initPlayer() {
            life = 100.0
            bullets = 15
        }
        fun resetPlayer() {
            initPlayer()
            lives = 1
            hasGun = false
            tookLife = false
        }
    }

    init {
        initPlayer()
        speed = 2
        moveAnimation = MoveAnimation(speed, 4)
        blinkAnimation = BlinkAnimation(10)
    }

    private val playerFront: List<BufferedImage> = loadPlayerImages(32)
    private val playerBack: List<BufferedImage> = loadPlayerImages(48)
    private val playerLeft: List<BufferedImage> = loadPlayerImages(64)
    private val playerRight: List<BufferedImage> = loadPlayerImages(80)
    private val playerDamage: List<BufferedImage> = listOf(
        SPRITE_SHEET.getSprite(96, 0, 16, 16),
        SPRITE_SHEET.getSprite(112, 0, 16, 16),
        SPRITE_SHEET.getSprite(128, 0, 16, 16),
        SPRITE_SHEET.getSprite(144, 0, 16, 16)
    )

    private fun loadPlayerImages(x: Int): List<BufferedImage> {
        return listOf(
            SPRITE_SHEET.getSprite(x, 0, 16, 16),
            SPRITE_SHEET.getSprite(x, 17, 16, 16),
            SPRITE_SHEET.getSprite(x, 0, 16, 16),
            SPRITE_SHEET.getSprite(x, 34, 16, 16)
        )
    }

    override fun update() {
        movePlayer()
        if (shoot && hasGun) shoot()
        moveAnimation.animate()
    }

    private fun shoot() {
        var bulletX: Int = 0
        var bulletY: Int = 0
        var directionX = 0
        var directionY = 0

        if (bullets >= 0) {
            bullets -= 1

            when (lastSide) {
                UP.ordinal -> {
                    directionY = -1
                    bulletX = x + 12
                    bulletY = y + 2
                }
                DOWN.ordinal -> {
                    directionY = 1
                    bulletX = x + 2
                    bulletY = y + 11
                }
                LEFT.ordinal -> {
                    directionX = -1
                    bulletX = x - 10
                    bulletY = y + 6
                }
                RIGHT.ordinal -> {
                    directionX = 1
                    bulletX = x + 16
                    bulletY = y + 6
                }
            }

            World.bullets.add(Bullet(bulletX , bulletY, 2, 2, directionX, directionY))
        }
        shoot = false
    }

    private fun movePlayer() {
        if (goUp && !isCollidingWithWall(x, y - speed)) y -= speed
        else if (goDown && !isCollidingWithWall(x, y + speed)) y += speed

        if (goRight && !isCollidingWithWall(x + speed, y)) x += speed
        else if (goLeft && !isCollidingWithWall(x - speed, y)) x -= speed
    }

    private fun isCollidingWithWall(nextX: Int, nextY: Int): Boolean {
        val playerNextBounds = Rectangle(nextX, nextY, width, height)

        World.walls.forEach { wall -> if (playerNextBounds.intersects(wall)) return true }

        return false
    }

    fun collectWeapon() {
        hasGun = true
    }

    override fun draw(graphics: Graphics) {
        if (goUp) {
            if (hasGun) graphicsDrawImage(REVOLVER_UP, graphics, y = y - 2)
            graphicsDrawImage(playerBack[moveAnimation.currentAnimationIndex], graphics)
        } else if (goDown) {
            graphicsDrawImage(playerFront[moveAnimation.currentAnimationIndex], graphics)
            if (hasGun) graphicsDrawImage(REVOLVER_DOWN, graphics, x - 8)
        } else if (goLeft) {
            graphicsDrawImage(playerLeft[moveAnimation.currentAnimationIndex], graphics)
            if (hasGun) graphicsDrawImage(REVOLVER_LEFT, graphics, x - 8, y - 2)
        } else if (goRight) {
            graphicsDrawImage(playerRight[moveAnimation.currentAnimationIndex], graphics)
            if (hasGun) graphicsDrawImage(REVOLVER_RIGHT, graphics, x + 2, y - 2)
        } else drawLastSide(graphics)

        if (tookDamage) drawPlayerDamage(graphics)
    }

    private fun drawLastSide(graphics: Graphics) {
        when (lastSide) {
            UP.ordinal -> {
                if (hasGun) graphicsDrawImage(REVOLVER_UP, graphics, y = y - 2)
                graphicsDrawImage(playerBack[0], graphics)
            }

            DOWN.ordinal -> {
                graphicsDrawImage(playerFront[0], graphics)
                if (hasGun) graphicsDrawImage(REVOLVER_DOWN, graphics, x - 8)
            }

            LEFT.ordinal -> {
                graphicsDrawImage(playerLeft[0], graphics)
                if (hasGun) graphicsDrawImage(REVOLVER_LEFT, graphics, x - 8, y - 2)
            }

            RIGHT.ordinal -> {
                graphicsDrawImage(playerRight[0], graphics)
                if (hasGun) graphicsDrawImage(REVOLVER_RIGHT, graphics, x + 2, y - 2)
            }
        }
    }

    private fun drawPlayerDamage(graphics: Graphics) {
        when (lastSide) {
            UP.ordinal -> graphicsDrawImage(playerDamage[1], graphics)
            DOWN.ordinal -> graphicsDrawImage(playerDamage[0], graphics)
            LEFT.ordinal -> graphicsDrawImage(playerDamage[2], graphics)
            RIGHT.ordinal -> graphicsDrawImage(playerDamage[3], graphics)
        }

        tookDamage = blinkAnimation.animate()

    }

    private fun graphicsDrawImage(image: BufferedImage, graphics: Graphics, x: Int = this.x, y: Int = this.y) {
        graphics.drawImage(image, x - Camera.x, y - Camera.y, width, height, null)
    }

    fun loseLife(damage: Int) {
        tookDamage = true
        if (life > 0) life -= damage
        if (life > 0) println("entities.Player life: $life")
    }

    fun die() {
        println("entities.Player is dead")
        if (lives > 1) {
            lives--
            WORLD.restartWorld()
        } else if (lives == 1)
            gameState = GAME_OVER

        return

    }
}