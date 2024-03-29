package com.github.bun133.mcpacker.defaultPacker

import com.github.bun133.mcpacker.*
import java.io.File

/**
 * DataPackPacker
 * データパックのpack.mcmetaを生成するためのPacker
 *
 * @see DataPackPackerEntry
 */
class DataPackPacker : DefaultPacker<Boolean>() {
    override fun abstractInit() {
        // 何もしない
    }

    override fun isSupported(entry: PackEntry): Boolean {
        return entry is DataPackPackerEntry
    }

    override fun packAll(entries: List<PackEntry>, packerTargetFolder: File): List<PackResult<Boolean>> {
        val dataPackEntries = entries.filterIsInstance<DataPackPackerEntry>()
        if (dataPackEntries.size > 1) {
            return listOf(
                PackResult(
                    false,
                    IllegalArgumentException("DataPackPacker can pack only one DataPackPackerEntry")
                )
            )
        }
        if (dataPackEntries.size == 1) {
            val en = dataPackEntries[0]

            val packerTarget = en.location().file(packerTargetFolder)
            packerTarget.parentFile.mkdirs()

            val str = en.packMeta()
            packerTarget.writeText(str)

            return listOf(PackResult(true))
        }

        return listOf()
    }
}

/**
 * DataPackPacker用のエントリ
 *
 * @param packFormat pack.mcmetaのpack_format
 */
class DataPackPackerEntry(
    val packFormat: Int = 6,
    val description: String = "Generated by MCPacker",
) : PackEntry() {
    override fun packEntryName(): String = "datapack-root"
    private val loc: InsidePackLocation = packLocation("pack.mcmeta")
    override fun location(): PackLocation = loc

    internal fun packMeta(): String {
        return """
            {
              "pack": {
                "pack_format": ${packFormat},
                "description": "$description"
              }
            }
        """.trimIndent()
    }
}