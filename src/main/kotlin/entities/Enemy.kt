package entities

import Animation
import Game
import Game.Companion.PLAYER
import Game.Companion.SPRITE_SHEET
import Player
import SidesEnum.*
import world.*
import java.awt.Graphics
import java.awt.Rectangle
import java.awt.image.BufferedImage

class Enemy(sprite: BufferedImage, x: Int, y: Int) : Entity(sprite, x, y) {

    private val damage: Int = 5
    private var lastSide: Int = DOWN.ordinal
    private val animation: Animation

    init {
        speed = 2
        animation = Animation(speed, 4)
    }

    private val enemyFront: List<BufferedImage> = listOf(
        SPRITE_SHEET.getSprite(32, 50, 16, 16),
        SPRITE_SHEET.getSprite(32, 67, 16, 16),
        SPRITE_SHEET.getSprite(32, 50, 16, 16),
        SPRITE_SHEET.getSprite(32, 84, 16, 16)
    )
    private val enemyBack: List<BufferedImage> = listOf(
        SPRITE_SHEET.getSprite(48, 50, 16, 16),
        SPRITE_SHEET.getSprite(48, 67, 16, 16),
        SPRITE_SHEET.getSprite(48, 50, 16, 16),
        SPRITE_SHEET.getSprite(48, 84, 16, 16)
    )
    private val enemyLeft: List<BufferedImage> = listOf(
        SPRITE_SHEET.getSprite(64, 50, 16, 16),
        SPRITE_SHEET.getSprite(64, 67, 16, 16),
        SPRITE_SHEET.getSprite(64, 50, 16, 16),
        SPRITE_SHEET.getSprite(64, 84, 16, 16)
    )
    private val enemyRight: List<BufferedImage> = listOf(
        SPRITE_SHEET.getSprite(80, 50, 16, 16),
        SPRITE_SHEET.getSprite(80, 67, 16, 16),
        SPRITE_SHEET.getSprite(80, 50, 16, 16),
        SPRITE_SHEET.getSprite(80, 84, 16, 16)
    )

    override fun update() {
        collidingWithFloor(this@Enemy)
        if (Game.random.nextInt(100) < 50) move()
    }

    private fun collidingWithFloor(rectangle: Rectangle) = World.floors.forEach { floor -> floor.collision(rectangle) }

    private fun move() {
        if (x < PLAYER.x && !isColliding(x + speed, y)) {
            x += speed
            lastSide = RIGHT.ordinal
        }
        if (x > PLAYER.x && !isColliding(x - speed, y)) {
            x -= speed
            lastSide = LEFT.ordinal
        }
        if (y < PLAYER.y && !isColliding(x, y + speed)) {
            y += speed
            lastSide = DOWN.ordinal
        }
        if (y > PLAYER.y && !isColliding(x, y - speed)) {
            y -= speed
            lastSide = UP.ordinal
        }
        animation.animate()
    }

    private fun isColliding(nextX: Int, nextY: Int): Boolean {
        val enemyNextBounds = Rectangle(nextX, nextY, width, height)

        collidingWithFloor(enemyNextBounds)

        if (enemyNextBounds.intersects(PLAYER)) { attackPlayer(); return true }

        World.walls.forEach { wall -> if (enemyNextBounds.intersects(wall)) return true }

        World.enemies.forEach { enemy -> if (enemy != this@Enemy && enemyNextBounds.intersects(enemy)) return true }

        return false
    }

    private fun attackPlayer() {
        if (Game.random.nextInt(100) <= 10)
            if (Player.life > damage) PLAYER.loseLife(this@Enemy.damage)
            else {
                PLAYER.loseLife(this@Enemy.damage)
                PLAYER.die()
            }
    }

    override fun draw(graphics: Graphics) {
        when (lastSide) {
            UP.ordinal -> graphicsDrawImage(enemyBack[animation.currentAnimationIndex], graphics)
            DOWN.ordinal -> graphicsDrawImage(enemyFront[animation.currentAnimationIndex], graphics)
            LEFT.ordinal -> graphicsDrawImage(enemyLeft[animation.currentAnimationIndex], graphics)
            RIGHT.ordinal -> graphicsDrawImage(enemyRight[animation.currentAnimationIndex], graphics)
        }
        drawLastSide(graphics)
    }

    private fun drawLastSide(graphics: Graphics) {
        when (lastSide) {
            UP.ordinal -> graphicsDrawImage(enemyBack[0], graphics)
            DOWN.ordinal -> graphicsDrawImage(enemyFront[0], graphics)
            LEFT.ordinal -> graphicsDrawImage(enemyLeft[0], graphics)
            RIGHT.ordinal -> graphicsDrawImage(enemyRight[0], graphics)
        }
    }

    private fun graphicsDrawImage(image: BufferedImage, graphics: Graphics) {
        graphics.drawImage(image, x - Camera.x, y - Camera.y, null)
    }

}