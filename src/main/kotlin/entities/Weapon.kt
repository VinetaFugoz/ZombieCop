package entities

import world.World
import world.World.Companion.player
import java.awt.image.BufferedImage

class Weapon(sprite: BufferedImage, x: Int, y: Int) : Entity(sprite, x, y) {

    companion object {
        fun collision() {
            World.weapons.forEach { weapon ->
                if (weapon.intersects(player)) {
                    player.collectWeapon()
                    World.weapons.remove(weapon)
                    return
                }
            }
        }
    }
}