package entities

import Game.Companion.PLAYER
import world.World
import java.awt.image.BufferedImage

class Ammo(sprite: BufferedImage, x: Int, y: Int) : Entity(sprite, x, y) {
    companion object {
        private const val STORED_AMMO = 5

        fun collision() {
            World.ammunition.forEach { ammo ->
                if (ammo.intersects(PLAYER)) {
                    setAmmunition()
                    World.ammunition.remove(ammo)
                    return
                }
            }
        }

        private fun setAmmunition() {
            Player.bullets += STORED_AMMO
            if (Player.bullets > Player.MAX_BULLETS) {
                val difference: Int = Player.bullets - Player.MAX_BULLETS
                Player.bullets -= difference
            }
        }
    }

}