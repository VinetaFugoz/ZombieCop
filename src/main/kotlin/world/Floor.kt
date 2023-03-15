package world

import entities.Enemy
import world.World.Companion.player
import java.awt.Rectangle
import java.awt.image.BufferedImage

class Floor(sprite: BufferedImage, x: Int, y: Int) : Tile(sprite, x, y) {

    companion object {
        fun collision() {
            World.floors.forEach { floor ->
                if (World.enemies.isEmpty()) collisionWithThis(player, floor)
                else World.enemies.forEach { enemy -> collisionWithThis(enemy, floor) }
            }
        }

        private fun collisionWithThis(rectangle: Rectangle, floor: Floor) {
            if (floor.intersects(rectangle) || floor.intersects(player)) {
                floor.sprite = FLOOR[1]
            } else {
                floor.sprite = FLOOR[0]
            }
        }
    }
}