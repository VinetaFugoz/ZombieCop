package entities

import world.World
import world.World.Companion.player
import java.awt.image.BufferedImage

class Ammo(sprite: BufferedImage, x: Int, y: Int) : Entity(sprite, x, y) {
    companion object {
        private const val STORED_AMMO = 10

        fun collision() {
            World.ammunition.forEach { ammo ->
                if (ammo.intersects(player)) {
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