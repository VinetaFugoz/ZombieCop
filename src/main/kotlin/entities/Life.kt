package entities

import world.World
import java.awt.image.BufferedImage

class Life(sprite: BufferedImage, x: Int, y: Int, var enabled: Boolean) : Entity(sprite, x, y) {
    companion object {
        fun collision() {
            World.lives.filter{life -> life.enabled}.forEach { life ->
                if (life.intersects(World.player)) {
                    setLife()
                    life.enabled = false
                    return
                }
            }
        }

        private fun setLife() {
            Player.lives++
            Player.tookLife = true
            if (Player.lives > Player.MAX_LIVES) {
                val difference: Int = Player.lives - Player.MAX_LIVES
                Player.lives -= difference
            }
        }
    }
}