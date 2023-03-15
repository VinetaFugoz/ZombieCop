package entities

import Animation
import Game
import Game.Companion.SPRITE_SHEET
import SidesEnum.*
import world.*
import world.World.Companion.player
import java.awt.Graphics
import java.awt.Rectangle
import java.awt.image.BufferedImage

class Enemy(sprite: BufferedImage, x: Int, y: Int) : Entity(sprite, x, y) {

    private var tookDamage: Boolean = false
    private val damage: Int = 5
    private var lastSide: Int = DOWN.ordinal
    private val animation: Animation
    var life: kotlin.Double = 0.0


    init {
        life = 20.0
        speed = 2
        animation = Animation(speed, 4)
    }

    private val enemyFront: List<BufferedImage> = loadEnemyImages(32)
    private val enemyBack: List<BufferedImage> = loadEnemyImages(48)
    private val enemyLeft: List<BufferedImage> = loadEnemyImages(32)
    private val enemyRight: List<BufferedImage> = loadEnemyImages(80)
    private val enemyDamage: List<BufferedImage> = listOf(
        SPRITE_SHEET.getSprite(96, 51, 16, 16),
        SPRITE_SHEET.getSprite(112, 51, 16, 16),
        SPRITE_SHEET.getSprite(128, 51, 16, 16),
        SPRITE_SHEET.getSprite(144, 51, 16, 16)
    )

    private fun loadEnemyImages(x: Int): List<BufferedImage> {
        return listOf(
            SPRITE_SHEET.getSprite(x, 51, 16, 16),
            SPRITE_SHEET.getSprite(x, 68, 16, 16),
            SPRITE_SHEET.getSprite(x, 51, 16, 16),
            SPRITE_SHEET.getSprite(x, 85, 16, 16)
        )
    }

    override fun update() {
        if (Game.random.nextInt(100) < 50) move()
    }

    private fun move() {
        if (player.y in (y - 32)..y || player.y in y..(y + 48)) {
            if (x < player.x && !isColliding(x + speed, y)) {
                x += speed
                lastSide = RIGHT.ordinal
            }
            if (x > player.x && !isColliding(x - speed, y)) {
                x -= speed
                lastSide = LEFT.ordinal
            }
            if (y < player.y && !isColliding(x, y + speed)) {
                y += speed
                lastSide = DOWN.ordinal
            }
            if (y > player.y && !isColliding(x, y - speed)) {
                y -= speed
                lastSide = UP.ordinal
            }
            animation.animate()
        }
    }

    private fun isColliding(nextX: Int, nextY: Int): Boolean {
        val enemyNextBounds = Rectangle(nextX, nextY, width, height)

        if (enemyNextBounds.intersects(player)) {
            attackPlayer(); return true
        }

        World.walls.forEach { wall -> if (enemyNextBounds.intersects(wall)) return true }

        World.enemies.forEach { enemy -> if (enemy != this@Enemy && enemyNextBounds.intersects(enemy)) return true }

        return false
    }

    private fun attackPlayer() {
        if (Game.random.nextInt(100) <= 10)
            if (Player.life > damage) player.loseLife(this@Enemy.damage)
            else {
                player.loseLife(this@Enemy.damage)
                player.die()
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
        if (tookDamage) drawEnemyDamage(graphics)
    }

    private fun drawEnemyDamage(graphics: Graphics) {
        when (lastSide) {
            UP.ordinal -> graphicsDrawImage(enemyDamage[1], graphics)
            DOWN.ordinal -> graphicsDrawImage(enemyDamage[0], graphics)
            LEFT.ordinal -> graphicsDrawImage(enemyDamage[2], graphics)
            RIGHT.ordinal -> graphicsDrawImage(enemyDamage[3], graphics)
        }
        tookDamage = false
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

    fun loseLife(damage: Int) {
        tookDamage = true
        life -= damage
        if (life > 0) println("Enemy life: ${Player.life}")
    }

    fun die() {
        println("Enemy is dead")
        World.enemies.remove(this@Enemy)
    }
}