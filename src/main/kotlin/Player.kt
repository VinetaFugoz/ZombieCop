import Game.Companion.SPRITE_SHEET
import SidesEnum.*
import entities.Ammo
import entities.Entity
import entities.LifePack
import world.*
import java.awt.Graphics
import java.awt.Rectangle
import java.awt.image.BufferedImage
import kotlin.system.exitProcess

class Player(sprite: BufferedImage, x: Int, y: Int) : Entity(sprite, x, y) {

    var goUp: Boolean = false
    var goDown: Boolean = false
    var goLeft: Boolean = false
    var goRight: Boolean = false

    var lastSide: Int = DOWN.ordinal

    private val animation: Animation

    companion object {
        var life: kotlin.Double = 100.0
        var bullets: Int = 15

        const val MAX_LIFE: kotlin.Double = 100.0
        const val MAX_BULLETS: Int = 20
    }

    init {
        speed = 2
        animation = Animation(speed, 4)
    }

    private val playerFront: List<BufferedImage> = listOf(
        SPRITE_SHEET.getSprite(32, 0, 16, 16),
        SPRITE_SHEET.getSprite(32, 17, 16, 16),
        SPRITE_SHEET.getSprite(32, 0, 16, 16),
        SPRITE_SHEET.getSprite(32, 34, 16, 16)
    )
    private val playerBack: List<BufferedImage> = listOf(
        SPRITE_SHEET.getSprite(48, 0, 16, 16),
        SPRITE_SHEET.getSprite(48, 17, 16, 16),
        SPRITE_SHEET.getSprite(48, 0, 16, 16),
        SPRITE_SHEET.getSprite(48, 34, 16, 16)
    )
    private val playerLeft: List<BufferedImage> = listOf(
        SPRITE_SHEET.getSprite(64, 0, 16, 16),
        SPRITE_SHEET.getSprite(64, 17, 16, 16),
        SPRITE_SHEET.getSprite(64, 0, 16, 16),
        SPRITE_SHEET.getSprite(64, 34, 16, 16)
    )
    private val playerRight: List<BufferedImage> = listOf(
        SPRITE_SHEET.getSprite(80, 0, 16, 16),
        SPRITE_SHEET.getSprite(80, 17, 16, 16),
        SPRITE_SHEET.getSprite(80, 0, 16, 16),
        SPRITE_SHEET.getSprite(80, 34, 16, 16)
    )

    override fun update() {
        collidingWithFloor(this@Player)
        movePlayer()
        animation.animate()
    }

    private fun collidingWithFloor(rectangle: Rectangle) = World.floors.forEach { floor -> floor.collision(rectangle) }

    private fun movePlayer() {
        if (goUp && !isColliding(x, y - speed)) y -= speed
        else if (goDown && !isColliding(x, y + speed)) y += speed

        if (goRight && !isColliding(x + speed, y)) x += speed
        else if (goLeft && !isColliding(x - speed, y)) x -= speed
    }

    private fun isColliding(nextX: Int, nextY: Int): Boolean {
        val playerNextBounds = Rectangle(nextX, nextY, width, height)

        collidingWithFloor(this@Player)

        World.walls.forEach { wall -> if (playerNextBounds.intersects(wall)) return true }

        World.lifePacks.forEach { lifePack ->
            if (playerNextBounds.intersects(lifePack)) {
                    receiveLifeFromLifePack()
                    World.lifePacks.remove(lifePack)
                    return false
                }
            }

        World.ammunition.forEach { ammo ->
            if (playerNextBounds.intersects(ammo)) {
                receiveAmmoFromAmmunition()
                World.ammunition.remove(ammo)
                return false
            }
        }

        return false
    }


    private fun receiveLifeFromLifePack() {
        life += LifePack.STORED_LIFE
        if (life > MAX_LIFE) {
            val difference: kotlin.Double = life - MAX_LIFE
            life -= difference
        }
    }

    private fun receiveAmmoFromAmmunition() {
        bullets += Ammo.STORED_AMMO
        if (bullets > MAX_BULLETS) {
            val difference: Int = bullets - MAX_BULLETS
            bullets -= difference
        }
    }

    override fun draw(graphics: Graphics) {
        if (goUp) graphicsDrawImage(playerBack[animation.currentAnimationIndex], graphics)
        else if (goDown) graphicsDrawImage(playerFront[animation.currentAnimationIndex], graphics)
        else if (goLeft) graphicsDrawImage(playerLeft[animation.currentAnimationIndex], graphics)
        else if (goRight) graphicsDrawImage(playerRight[animation.currentAnimationIndex], graphics)
        else drawLastSide(graphics)
    }

    private fun drawLastSide(graphics: Graphics) {
        when (lastSide) {
            UP.ordinal -> graphicsDrawImage(playerBack[0], graphics)
            DOWN.ordinal -> graphicsDrawImage(playerFront[0], graphics)
            LEFT.ordinal -> graphicsDrawImage(playerLeft[0], graphics)
            RIGHT.ordinal -> graphicsDrawImage(playerRight[0], graphics)
        }
    }

    private fun graphicsDrawImage(image: BufferedImage, graphics: Graphics) {
        graphics.drawImage(image, x - Camera.x, y - Camera.y, width, height, null)
    }

    fun die() {
        println("Player is dead")
        exitProcess(0)
    }

    fun loseLife(damage: Int) {
        life -= damage
        if (life > 0) println("Player life: $life")
    }
}