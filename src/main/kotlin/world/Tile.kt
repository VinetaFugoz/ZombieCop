package world

import Game
import java.awt.Color
import java.awt.Graphics
import java.awt.Rectangle
import java.awt.image.BufferedImage

open class Tile(
    var sprite: BufferedImage,
    x: Int,
    y: Int,
    width: Int = TILE_SIZE,
    height: Int = TILE_SIZE
) : Rectangle(x, y, width, height) {

    companion object {
        const val TILE_SIZE = 16

        val FLOOR: Array<BufferedImage> = arrayOf(
            Game.SPRITE_SHEET.getSprite(0, 0, TILE_SIZE, TILE_SIZE),
            Game.SPRITE_SHEET.getSprite(0, 17, TILE_SIZE, TILE_SIZE)
        )

        val WALL: BufferedImage = Game.SPRITE_SHEET.getSprite(16, 0, TILE_SIZE, TILE_SIZE)
    }

    fun draw(graphics: Graphics) {
        graphics.drawImage(sprite, (x - Camera.x), (y - Camera.y), width, width, null)
    }
}