package entities

import world.World
import world.World.Companion.player
import java.awt.image.BufferedImage

class LifePack(sprite: BufferedImage, x: Int, y: Int) : Entity(sprite, x, y) {
    companion object {
        private const val STORED_LIFE = 20

        fun collision() {
            World.lifePacks.forEach { lifePack ->
                if (lifePack.intersects(player)) {
                    setLife()
                    World.lifePacks.remove(lifePack)
                    return
                }
            }
        }

        private fun setLife() {
            Player.life += STORED_LIFE
            if (Player.life > Player.MAX_LIFE) {
                val difference: kotlin.Double = Player.life - Player.MAX_LIFE
                Player.life -= difference
            }
        }
    }
}