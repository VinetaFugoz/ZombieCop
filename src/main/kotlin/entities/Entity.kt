package entities

import Game
import world.Camera
import java.awt.Color
import java.awt.Graphics
import java.awt.Rectangle
import java.awt.image.BufferedImage

open class Entity(
    val sprite: BufferedImage,
    x: Int,
    y: Int,
    width: Int = 16,
    height: Int = 16,
) : Rectangle(x, y, width, height) {

    var speed: Int = 0

    companion object {
        val LIFE_PACK = Game.SPRITE_SHEET.getSprite(95, 0, 16, 16)
        val WEAPON = Game.SPRITE_SHEET.getSprite(112, 0, 16, 16)
        val AMMO = Game.SPRITE_SHEET.getSprite(112, 16, 16, 16)
        val ENEMY = Game.SPRITE_SHEET.getSprite(32, 51, 16, 16)
    }

    open fun update() {
        // we are not using this fun for now
    }

    open fun draw(graphics: Graphics) {
        graphics.drawImage(sprite, x - Camera.x, y - Camera.y, width, height, null)
        graphics.color = Color.BLUE
        graphics.drawRect(x, y, width, height)
    }
}

