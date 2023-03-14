package graphics

import Game.Companion.BULLET
import entities.Player
import java.awt.*

class UI {

    fun draw(graphics: Graphics2D) {
        drawLifeBar(graphics)
        drawAmmoBar(graphics)
    }

    private fun drawLifeBar(graphics: Graphics) {
        graphics.color = Color.GRAY
        graphics.fillRect(4, 4, ((100 / Player.MAX_LIFE) * 50 + 2).toInt(), 10)

        graphics.color = Color.RED
        graphics.fillRect(5, 5, ((100 / Player.MAX_LIFE) * 50).toInt(), 8)

        graphics.color = Color.GREEN
        graphics.fillRect(5, 5, ((Player.life / Player.MAX_LIFE) * 50).toInt(), 8)

        graphics.color = Color.BLACK
        graphics.font = Font("arial", Font.BOLD, 8)
        graphics.drawString("${Player.life.toInt()}/${Player.MAX_LIFE.toInt()}", 7, 12)
    }

    private fun drawAmmoBar(graphics: Graphics2D) {
        graphics.composite = (AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.5f))

        for (bullet: Int in 0..Player.MAX_BULLETS) {
            graphics.drawImage(BULLET, 5 + (bullet * 2), 15, 1, 6,null)
        }

        graphics.composite = (AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f))

        for (bullet: Int in 0..Player.bullets) {
            graphics.drawImage(BULLET, 5 + (bullet * 2), 15, 1, 6, null)
        }
    }
}