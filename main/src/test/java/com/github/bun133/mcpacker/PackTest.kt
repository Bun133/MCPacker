package com.github.bun133.mcpacker

import com.github.bun133.mcpacker.defaultPacker.BlockTexturePackerEntry
import com.github.bun133.mcpacker.defaultPacker.DataPackPackerEntry
import com.github.bun133.mcpacker.defaultPacker.ItemTexturePackerEntry
import org.bukkit.Material
import org.junit.Test
import java.io.File
import java.nio.file.Paths
import javax.imageio.ImageIO
import kotlin.time.ExperimentalTime
import kotlin.time.measureTime

class PackTest {
    private val testTopDir = Paths.get("./src/test/resources/test").toFile()
    private val resultTopDir = Paths.get("./src/test/resources/test/result").toFile()
    private val expectedTopDir = Paths.get("./src/test/resources/test/expected").toFile()
    private val assetTopDir = Paths.get("./src/test/resources/test/asset").toFile()

    @Test
    fun generateBaseResourcePack() {
        runTestWithName("base", listOf(DataPackPackerEntry(description = "Test")))
    }

    @Test
    fun generateBlockTexture() {
        runTestWithName(
            "block",
            listOf(
                DataPackPackerEntry(description = "Test"),
                BlockTexturePackerEntry(Material.STONE, ImageIO.read(assetTopDir.resolve("test.png")))
            )
        )
    }

    @Test
    fun generateAllTexture() {
        val img = ImageIO.read(assetTopDir.resolve("test.png"))
        runTestWithName(
            "all",
            listOf(
                DataPackPackerEntry(description = "Test"),
            ) + Material.values().filter { !it.isLegacy }.map { mat ->
                if (mat.isBlock) {
                    return@map BlockTexturePackerEntry(mat, img)
                } else {
                    return@map ItemTexturePackerEntry(mat, img)
                }
            }
        )
    }

    @OptIn(ExperimentalTime::class)
    private fun runTestWithName(name: String, entries: List<PackEntry<*>>) {
        val targetDir = File(resultTopDir, name)
        if (targetDir.exists()) {
            targetDir.deleteRecursively()
        }
        val time = measureTime {
            assertAndPack(targetDir, entries)
        }

        println("Test $name took $time")
    }

    private fun assertAndPack(targetDir: File, entries: List<PackEntry<*>>): Boolean {
        val b = pack(targetDir, entries)
        assert(b) { "generate Resource Pack" }
        assertSame(targetDir, File(expectedTopDir, targetDir.name))
        return b
    }

    private fun assertSame(targetDir: File, expectedDir: File) {
        assert(isSame(targetDir, expectedDir)) { "generated Data pack is not same as expected!" }
    }

    private fun isSame(directory1: File, directory2: File): Boolean {
        if (!directory1.exists() || !directory2.exists()) return false
        if (directory1.isDirectory && directory2.isDirectory) {
            val list1 = directory1.listFiles() ?: return false
            val list2 = directory2.listFiles() ?: return false
            if (list1.size != list2.size) {
                return false
            }
            for (i in list1.indices) {
                if (!isSame(list1[i], list2[i])) {
                    return false
                }
            }
            return true
        } else {
            return directory1.readBytes().contentEquals(directory2.readBytes())
        }
    }
}