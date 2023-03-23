package entities

import Game.Companion.SPRITE_SHEET
import world.Camera
import java.awt.Color
import java.awt.Graphics
import java.awt.Rectangle
import java.awt.image.BufferedImage

open class Entity(
    val sprite: BufferedImage = AMMO,
    x: Int,
    y: Int,
    width: Int = 16,
    height: Int = 16,
) : Rectangle(x, y, width, height) {

    var speed: Int = 0

    companion object {
        val AMMO = SPRITE_SHEET.getSprite(48, 119, 16, 16)
        val BULLET = SPRITE_SHEET.getSprite(32, 118, 16, 16)
        val ENEMY = SPRITE_SHEET.getSprite(32, 51, 16, 16)
        val LIFE_PACK = SPRITE_SHEET.getSprite(32, 102, 16, 16)
        val PLAYER = SPRITE_SHEET.getSprite(32, 0, 16, 16)
        val LIFE = SPRITE_SHEET.getSprite(112, 105, 16, 16)
        val REVOLVER_UP = SPRITE_SHEET.getSprite(83, 102, 16, 16)
        val REVOLVER_DOWN = SPRITE_SHEET.getSprite(97, 102, 16, 16)
        val REVOLVER_LEFT = SPRITE_SHEET.getSprite(66, 102, 16, 16)
        val REVOLVER_RIGHT = SPRITE_SHEET.getSprite(49, 102, 16, 16)
    }

    open fun update() {
        // we are not using this fun for now
    }

    open fun draw(graphics: Graphics) {
        graphics.drawImage(sprite, x - Camera.x, y - Camera.y, width, height, null)
    }
}

