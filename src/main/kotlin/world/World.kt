package world

import Game
import entities.*
import entities.Entity.Companion.AMMO
import entities.Entity.Companion.ENEMY
import entities.Entity.Companion.LIFE_PACK
import entities.Entity.Companion.WEAPON
import world.Tile.Companion.FLOOR
import world.Tile.Companion.TILE_SIZE
import world.Tile.Companion.WALL
import java.awt.Graphics
import java.awt.image.BufferedImage
import java.io.IOException
import javax.imageio.ImageIO

class World(path: String) {

    private var mapPixels: MutableList<Triple<Int, Int, Int>> = mutableListOf()

    companion object {
        var WIDTH: Int = 0
        var HEIGHT: Int = 0

        val ammunition: MutableList<Ammo> = mutableListOf()
        val enemies: MutableList<Enemy> = mutableListOf()
        val lifePacks: MutableList<LifePack> = mutableListOf()
        val weapons: MutableList<Weapon> = mutableListOf()
        val floors: MutableList<Floor> = mutableListOf()
        val walls: MutableList<Wall> = mutableListOf()

        lateinit var ammunitionMap: Map<Pair<Int, Int>, Ammo>
        lateinit var enemyMap: Map<Pair<Int, Int>, Enemy>
        lateinit var lifePackMap: Map<Pair<Int, Int>, LifePack>
        lateinit var weaponMap: Map<Pair<Int, Int>, Weapon>
        lateinit var floorMap: Map<Pair<Int, Int>, Floor>
        lateinit var wallMap: Map<Pair<Int, Int>, Wall>
    }

    init {
        try {
            val map: BufferedImage = ImageIO.read(javaClass.getResource(path))
            addMapPixels(map)
        } catch (exception: IOException) {
            exception.printStackTrace()
        }

        addTilesAndEntities()
    }

    private fun addMapPixels(map: BufferedImage) {

        map.run {
            WIDTH = width
            HEIGHT = height

            for (x: Int in 0 until WIDTH) {
                for (y: Int in 0 until HEIGHT) {
                    mapPixels.add(Triple(getRGB(x, y), x * TILE_SIZE, y * TILE_SIZE))
                }
            }

            Camera.MAX_X = mapPixels[mapPixels.size - 1].second - Game.WIDTH + TILE_SIZE
            Camera.MAX_Y = mapPixels[mapPixels.size - 1].third - Game.HEIGHT + TILE_SIZE
        }
    }

    private fun addTilesAndEntities() {
        mapPixels.forEach { pixel ->
            pixel.run {
                when (first) {
                    0xFF0026FF.toInt() -> {Game.PLAYER.x = second; Game.PLAYER.y = third}
                    0xFFFF6A00.toInt() -> weapons.add(Weapon(WEAPON, second, third))
                    0xFFFF0000.toInt() -> enemies.add(Enemy(ENEMY, second, third))
                    0xFF00FF21.toInt() -> lifePacks.add(LifePack(LIFE_PACK, second, third))
                    0xFFFFD800.toInt() -> ammunition.add(Ammo(AMMO, second, third))
                    0xFFFFFFFF.toInt() -> walls.add(Wall(WALL, second, third))
                }

                if (first != 0xFFFFFFFF.toInt()) floors.add(Floor(FLOOR[0], second, third))
            }
        }
    }

    fun update() {
        enemies.forEach { enemy -> enemy.update() }
    }

    fun draw(graphics: Graphics) {
        fillTilesAndEntitiesMap()

        drawObjectsInCamera(wallMap, graphics)
        drawObjectsInCamera(floorMap, graphics)
        drawObjectsInCamera(weaponMap, graphics)
        drawObjectsInCamera(lifePackMap, graphics)
        drawObjectsInCamera(ammunitionMap, graphics)
        drawObjectsInCamera(enemyMap, graphics)
    }

    private fun fillTilesAndEntitiesMap() {
        ammunitionMap = ammunition.associateBy { ammo -> ammo.x to ammo.y }
        enemyMap = enemies.associateBy { enemy -> enemy.x to enemy.y }
        lifePackMap = lifePacks.associateBy { lifePack -> lifePack.x to lifePack.y }
        weaponMap = weapons.associateBy { weapon -> weapon.x to weapon.y }
        floorMap = floors.associateBy { floor -> floor.x to floor.y }
        wallMap = walls.associateBy { wall -> wall.x to wall.y }
    }

    private fun drawObjectsInCamera(map: Map<Pair<Int, Int>, Any>, graphics: Graphics) {
        for (cameraX in Camera.x - TILE_SIZE..Camera.x + Game.WIDTH) {
            for (cameraY in Camera.y - TILE_SIZE..Camera.y + Game.HEIGHT) {
                map[cameraX to cameraY]?.let {obj ->
                    if (obj is Tile) obj.draw(graphics)
                    else (obj as Entity).draw(graphics)
                }
            }
        }
    }
}