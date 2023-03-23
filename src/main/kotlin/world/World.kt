package world

import Game
import entities.*
import entities.Entity.Companion.AMMO
import entities.Entity.Companion.ENEMY
import entities.Entity.Companion.LIFE
import entities.Entity.Companion.LIFE_PACK
import entities.Entity.Companion.PLAYER
import entities.Entity.Companion.REVOLVER_RIGHT
import world.Tile.Companion.DOOR
import world.Tile.Companion.FLOOR
import world.Tile.Companion.TILE_SIZE
import world.Tile.Companion.WALL
import java.awt.Graphics
import java.awt.image.BufferedImage
import java.io.IOException
import javax.imageio.ImageIO

class World(var level: String) {

    companion object {
        private var mapPixels: MutableList<Triple<Int, Int, Int>> = mutableListOf()

        lateinit var player: Player
        lateinit var bullets: MutableList<Bullet>
        lateinit var ammunition: MutableList<Ammo>
        lateinit var enemies: MutableList<Enemy>
        lateinit var lifePacks: MutableList<LifePack>
        lateinit var weapons: MutableList<Weapon>
        lateinit var floors: MutableList<Floor>
        lateinit var walls: MutableList<Wall>
        lateinit var doors: MutableList<Door>
        lateinit var lives: MutableList<Life>

        lateinit var ammunitionMap: Map<Pair<Int, Int>, Ammo>
        lateinit var enemyMap: Map<Pair<Int, Int>, Enemy>
        lateinit var lifePackMap: Map<Pair<Int, Int>, LifePack>
        lateinit var weaponMap: Map<Pair<Int, Int>, Weapon>
        lateinit var floorMap: Map<Pair<Int, Int>, Floor>
        lateinit var wallMap: Map<Pair<Int, Int>, Wall>
        lateinit var doorMap: Map<Pair<Int, Int>, Door>
        var lifeMap: Map<Pair<Int, Int>, Life> = mapOf()
    }

    init {
        initWorld()
    }

    fun initWorld() {
        bullets = mutableListOf()
        ammunition = mutableListOf()
        enemies = mutableListOf()
        lifePacks = mutableListOf()
        weapons = mutableListOf()
        floors = mutableListOf()
        walls = mutableListOf()
        doors = mutableListOf()
        mapPixels = mutableListOf()
        lives = mutableListOf()

        setLevel()
        addTilesAndEntities()
    }

    fun restartWorld() {
        bullets = mutableListOf()
        ammunition = mutableListOf()
        enemies = mutableListOf()
        lifePacks = mutableListOf()
        weapons = mutableListOf()
        floors = mutableListOf()
        walls = mutableListOf()
        doors = mutableListOf()
        mapPixels = mutableListOf()
        if (lives.isEmpty()) lives = mutableListOf()

        setLevel()
        addTilesAndEntities()
    }

    private fun setLevel() {
        try {
            val map: BufferedImage = ImageIO.read(javaClass.getResource(level))
            addMapPixels(map)
        } catch (exception: IOException) {
            exception.printStackTrace()
        }
    }

    private fun addMapPixels(map: BufferedImage) {
        map.run {

            for (x: Int in 0 until width) {
                for (y: Int in 0 until height) {
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
                    0xFF0026FF.toInt() -> {
                        player = Player(PLAYER, second, third)
                    }

                    0xFFFF6A00.toInt() -> if (!Player.hasGun) weapons.add(Weapon(REVOLVER_RIGHT, second, third))
                    0xFFFF0000.toInt() -> enemies.add(Enemy(ENEMY, second, third))
                    0xFF00FF21.toInt() -> lifePacks.add(LifePack(LIFE_PACK, second, third))
                    0xFFFFD800.toInt() -> ammunition.add(Ammo(AMMO, second, third))
                    0xFFFFFFFF.toInt() -> walls.add(Wall(WALL, second, third))
                    0xFF7C4412.toInt() -> doors.add(Door(DOOR[0], second, third))
                    0xFFFF00D4.toInt() -> lifeMap[second to third].let {
                        if (it == null) lives.add(Life(LIFE, second, third, true))
                    }
                }

                if (first != 0xFFFFFFFF.toInt()) floors.add(Floor(FLOOR[0], second, third))
            }
        }
    }

    fun update() {
        enemies.forEach { enemy -> enemy.update() }
        bullets.forEach { bullet -> bullet.update() }
        player.update()
    }

    fun draw(graphics: Graphics) {
        fillTilesAndEntitiesMap()

        Floor.collision()
        Ammo.collision()
        LifePack.collision()
        Weapon.collision()
        Bullet.collision()
        Life.collision()
        Door.collision()

        drawObjectsInCamera(wallMap, graphics)
        drawObjectsInCamera(floorMap, graphics)
        drawObjectsInCamera(doorMap, graphics)
        drawObjectsInCamera(weaponMap, graphics)
        drawObjectsInCamera(lifePackMap, graphics)
        drawObjectsInCamera(ammunitionMap, graphics)
        drawObjectsInCamera(enemyMap, graphics)
        val lifeMapEnabled: Map<Pair<Int, Int>, Life> = lifeMap.filter { life -> life.value.enabled }
        drawObjectsInCamera(lifeMapEnabled, graphics)


        player.draw(graphics)
        bullets.forEach { bullet -> bullet.draw(graphics) }
    }

    private fun fillTilesAndEntitiesMap() {
        ammunitionMap = ammunition.associateBy { ammo -> ammo.x to ammo.y }
        enemyMap = enemies.associateBy { enemy -> enemy.x to enemy.y }
        lifePackMap = lifePacks.associateBy { lifePack -> lifePack.x to lifePack.y }
        weaponMap = weapons.associateBy { weapon -> weapon.x to weapon.y }
        floorMap = floors.associateBy { floor -> floor.x to floor.y }
        wallMap = walls.associateBy { wall -> wall.x to wall.y }
        lifeMap = lives.associateBy { life -> life.x to life.y }
        doorMap = doors.associateBy { door -> door.x to door.y }
    }

    private fun drawObjectsInCamera(map: Map<Pair<Int, Int>, Any>, graphics: Graphics) {
        for (cameraX in Camera.x - TILE_SIZE..Camera.x + Game.WIDTH) {
            for (cameraY in Camera.y - TILE_SIZE..Camera.y + Game.HEIGHT) {
                map[cameraX to cameraY]?.let { obj ->
                    if (obj is Tile) obj.draw(graphics)
                    else (obj as Entity).draw(graphics)
                }
            }
        }
    }
}