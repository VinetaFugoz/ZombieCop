package world

import java.awt.Rectangle
import java.awt.image.BufferedImage

class Floor(sprite: BufferedImage, x: Int, y: Int): Tile(sprite, x, y) {

    fun collision(rectangle: Rectangle) {
        if (rectangle.intersects(this@Floor)) {
            this@Floor.sprite = FLOOR[1]
        } else {
            this@Floor.sprite = FLOOR[0]
        }
    }
}