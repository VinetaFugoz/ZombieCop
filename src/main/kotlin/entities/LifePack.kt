package entities

import java.awt.image.BufferedImage

class LifePack(sprite: BufferedImage, x: Int, y: Int): Entity(sprite, x, y) {
    companion object {
        const val STORED_LIFE = 20
    }
}