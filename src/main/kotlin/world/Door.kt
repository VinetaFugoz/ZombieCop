package world

import world.World.Companion.doors
import world.World.Companion.player
import java.awt.image.BufferedImage

class Door(sprite: BufferedImage, x: Int, y: Int) : Tile(sprite, x, y) {

    companion object {
        fun collision() {
            doors.forEach { door -> collisionWithThis(door) }
        }

        private fun collisionWithThis(door: Door) {
            if (doors.indexOf(door) % 2 == 0) {
                if (door.intersects(player)) {
                    door.sprite = DOOR[1]
                    Game.goNextLevel = true
                } else door.sprite = DOOR[0]

            } else {
                if (door.intersects(player)) {
                    door.sprite = DOOR[2]
                    Game.goNextLevel = true
                } else door.sprite = DOOR[0]
            }
        }
    }
}