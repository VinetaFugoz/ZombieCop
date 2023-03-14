package entities

import Game.Companion.PLAYER
import world.World
import java.awt.image.BufferedImage

class Weapon(sprite: BufferedImage, x: Int, y: Int) : Entity(sprite, x, y) {

    companion object {
        fun collision() {
            World.weapons.forEach { weapon ->
                if (weapon.intersects(PLAYER)) {
                    PLAYER.collectWeapon()
                    World.weapons.remove(weapon)
                    return
                }
            }
        }
    }
}