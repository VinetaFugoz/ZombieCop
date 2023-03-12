package graphics

import java.awt.image.BufferedImage
import java.io.IOException
import javax.imageio.ImageIO

class SpriteSheet(path: String) {

    private lateinit var spriteSheet: BufferedImage

    init {
        try {
            spriteSheet = ImageIO.read(javaClass.getResource(path))
        } catch (exception: IOException) {
            exception.printStackTrace()
        }
    }

    fun getSprite(x: Int, y: Int, width: Int, height: Int): BufferedImage = spriteSheet.getSubimage(x, y, width, height)
}