package com.github.bun133.mcpacker.defaultPacker

import com.github.bun133.mcpacker.PackEntry
import com.github.bun133.mcpacker.PackResult
import com.github.bun133.mcpacker.packLocation
import com.github.bun133.mcpacker.toSuccessResult
import org.bukkit.Material
import java.awt.image.BufferedImage
import java.io.File

/**
 * ブロックのテクスチャをpackするPacker
 *
 * @see BlockTexturePackerEntry
 */
class BlockTexturePacker : DefaultPacker<Boolean>() {
    override fun abstractInit() {
        // 何もしない
    }

    override fun isSupported(entry: PackEntry): Boolean {
        return entry is BlockTexturePackerEntry
    }

    override fun packAll(entries: List<PackEntry>, packerTargetFolder: File): List<PackResult<Boolean>> {
        val entryList = entries.filterIsInstance<BlockTexturePackerEntry>()
        return entryList.map {
            passPack(
                ImagePackerEntry(
                    it.img,
                    it.packEntryName(),
                    packLocation(it.location().rawPathString)
                ), packerTargetFolder
            ).toSuccessResult()
        }
    }
}

/**
 * ブロックのテクスチャをpackするPackerのEntry
 *
 * @param block 対象のブロック
 * @param img テクスチャ
 */
class BlockTexturePackerEntry(
    private val block: Material,
    internal val img: BufferedImage,
) : PackEntry() {
    override fun packEntryName(): String = "[BlockTexture]${block.key.value()}"
    private val loc =
        packLocation("assets/minecraft/textures/block/${block.key.value()}.png")

    override fun location() = loc
}