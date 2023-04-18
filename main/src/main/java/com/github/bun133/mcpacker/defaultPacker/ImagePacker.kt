package com.github.bun133.mcpacker.defaultPacker

import com.github.bun133.mcpacker.MCPacker
import com.github.bun133.mcpacker.PackEntry
import com.github.bun133.mcpacker.PackLocation
import com.github.bun133.mcpacker.PackResult
import java.awt.image.BufferedImage
import java.io.File
import javax.imageio.ImageIO

/**
 * 画像をpackするPacker
 *
 * @see ImagePackerEntry
 */
class ImagePacker : DefaultPacker<Boolean>() {
    override fun abstractInit() {
        // 何もしない
    }

    override fun isSupported(entry: PackEntry): Boolean {
        return entry is ImagePackerEntry
    }

    override fun packAll(entries: List<PackEntry>, packerTargetFolder: File): List<PackResult<Boolean>> {
        val imageEntries = entries.filterIsInstance<ImagePackerEntry>()
        return imageEntries.map { en ->
            val packerTarget = en.location().file(packerTargetFolder)
            packerTarget.mkdirs()

            val img = en.img
            val format = en.formatString
            val result = ImageIO.write(img, format, packerTarget)
            if (result) {
                PackResult(true)
            } else {
                PackResult(false, IllegalArgumentException("ImagePacker can't write image"))
            }
        }
    }
}

/**
 * ImagePacker用のエントリ
 * @param img 保存するBufferedImage
 */
open class ImagePackerEntry(
    val img: BufferedImage,
    private val name: String,
    private val loc: PackLocation,
    val formatString: String = "png"
) :
    PackEntry() {
    override fun packEntryName(): String = name
    override fun location() = loc
}