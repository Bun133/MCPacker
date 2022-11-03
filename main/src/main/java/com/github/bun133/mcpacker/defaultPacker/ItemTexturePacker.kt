package com.github.bun133.mcpacker.defaultPacker

import com.github.bun133.mcpacker.PackEntry
import com.github.bun133.mcpacker.PackResult
import com.github.bun133.mcpacker.packLocation
import com.github.bun133.mcpacker.toSuccessResult
import org.bukkit.Material
import java.awt.image.BufferedImage
import java.io.File

class ItemTexturePacker : DefaultPacker<Boolean>() {
    override fun abstractInit() {
        // 何もしない
    }

    override fun isSupported(entry: PackEntry<*>): Boolean {
        return entry is ItemTexturePackerEntry
    }

    override fun packAll(entries: List<PackEntry<*>>, packerTargetFolder: File): List<PackResult<Boolean>> {
        val entryList = entries.filterIsInstance<ItemTexturePackerEntry>()
        return entryList.map {
            passPack<ImagePacker, Boolean>(
                ImagePackerEntry(
                    it.img,
                    it.packEntryName(),
                    packLocation(it.location().rawPathString)
                ), packerTargetFolder
            ).toSuccessResult()
        }
    }
}

class ItemTexturePackerEntry(
    private val item: Material,
    internal val img: BufferedImage,
) : PackEntry<BlockTexturePackerEntry>() {

    override fun packEntryName(): String = "[BlockTexture]${item.key.value()}"
    private val loc =
        packLocation<BlockTexturePackerEntry>("assets/minecraft/textures/item/${item.key.value()}.png")

    override fun location() = loc
}