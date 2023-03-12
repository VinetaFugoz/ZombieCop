package world

import Game
import Game.Companion.HEIGHT
import Game.Companion.PLAYER
import Game.Companion.WIDTH

object Camera {
    var x: Int = 0
    var y: Int = 0

    private const val MIN_X: Int = 0
    private const val MIN_Y: Int = 0
    var MAX_X: Int = 0
    var MAX_Y: Int = 0

    fun update() {
        x = (PLAYER.x - WIDTH / 2)
        y = (PLAYER.y - HEIGHT / 2)

        if (x < MIN_X) x = MIN_X
        if (y < MIN_Y) y = MIN_Y
        if (x > MAX_X) x = MAX_X
        if (y > MAX_Y) y = MAX_Y
    }
}
