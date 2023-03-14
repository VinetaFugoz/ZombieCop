package world

import Game
import entities.Enemy
import java.awt.image.BufferedImage

class Floor(sprite: BufferedImage, x: Int, y: Int): Tile(sprite, x, y) {

    companion object {
        fun collision() {
            World.floors.forEach { floor ->
                World.enemies.forEach { enemy ->
                    collisionWithThis(enemy, floor)
                }
            }
        }

        private fun collisionWithThis(enemy: Enemy, floor: Floor) {
            if (floor.intersects(enemy) || floor.intersects(Game.PLAYER)) {
                floor.sprite = FLOOR[1]
            } else {
                floor.sprite = FLOOR[0]
            }
        }
    }
}