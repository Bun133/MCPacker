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

    override fun isSupported(entry: PackEntry<*>): Boolean {
        return entry is BlockTexturePackerEntry
    }

    override fun packAll(entries: List<PackEntry<*>>, packerTargetFolder: File): List<PackResult<Boolean>> {
        val entryList = entries.filterIsInstance<BlockTexturePackerEntry>()
        return entryList.map {
            passPack<ImagePacker, Boolean>(
                ImagePackerEntry(
                    it.img,
                    it.packEntryName(),
                    packLocation(it.location().pathString())
                ), packerTargetFolder
            ).toSuccessResult()
        }
    }
}

class BlockTexturePackerEntry(
    private val block: Material,
    internal val img: BufferedImage,
) : PackEntry<BlockTexturePackerEntry>() {
    override fun packEntryName(): String = block.translationKey
    private val loc =
        packLocation<BlockTexturePackerEntry>("assets/minecraft/textures/block/${block.translationKey}.png")

    override fun location() = loc
}