package entities

import java.awt.image.BufferedImage

class Ammo(sprite: BufferedImage, x: Int, y: Int): Entity(sprite, x, y) {
    companion object {
        const val STORED_AMMO = 20
    }
}