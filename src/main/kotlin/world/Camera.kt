package world

import Game.Companion.HEIGHT
import Game.Companion.WIDTH
import world.World.Companion.player

object Camera {
    var x: Int = 0
    var y: Int = 0

    private const val MIN_X: Int = 0
    private const val MIN_Y: Int = 0
    var MAX_X: Int = 0
    var MAX_Y: Int = 0

    fun update() {
        x = (player.x - WIDTH / 2)
        y = (player.y - HEIGHT / 2)

        if (x < MIN_X) x = MIN_X
        if (y < MIN_Y) y = MIN_Y
        if (x > MAX_X) x = MAX_X
        if (y > MAX_Y) y = MAX_Y
    }
}
