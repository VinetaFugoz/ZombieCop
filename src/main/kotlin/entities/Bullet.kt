package entities

import world.Camera
import world.World
import java.awt.Color
import java.awt.Graphics

class Bullet(
    x: Int,
    y: Int,
    width: Int,
    height: Int,
    private val directionX: Int,
    private val directionY: Int
) : Entity(x = x, y = y, width = width, height = height) {
    companion object {

        private const val BULLET_DAMAGE = 4
        fun collision() {
            World.bullets.forEach { bullet ->
                World.enemies.forEach { enemy ->
                    if (bullet.intersects(enemy)) {
                        World.bullets.remove(bullet)
                        if (enemy.life > BULLET_DAMAGE) enemy.loseLife(BULLET_DAMAGE)
                        else {
                            enemy.loseLife(BULLET_DAMAGE)
                            enemy.die()
                        }
                        return
                    }
                }

                World.walls.forEach { wall ->
                    if (bullet.intersects(wall)) {
                        World.bullets.remove(bullet)
                        return
                    }
                }
            }
        }
    }
    init {
        speed = 5
    }
    override fun update() {
        x += directionX * speed
        y += directionY * speed
    }

    override fun draw(graphics: Graphics) {
        graphics.color = Color.YELLOW
        graphics.fillRect(x - Camera.x, y - Camera.y, width, height)
    }
}