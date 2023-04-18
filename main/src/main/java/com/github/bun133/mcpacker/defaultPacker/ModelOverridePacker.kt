package com.github.bun133.mcpacker.defaultPacker

import com.github.bun133.mcpacker.PackEntry
import com.github.bun133.mcpacker.PackLocation
import com.github.bun133.mcpacker.PackResult
import com.github.bun133.mcpacker.toSuccessResult
import java.io.File

/**
 * ResourcePack/models内の、jsonファイルの中の、overridesをいじくるやつ
 */
class ModelOverridePacker : DefaultPacker<Boolean>() {
    override fun abstractInit() {
        // DO NOTHING
    }

    override fun isSupported(entry: PackEntry): Boolean {
        return entry is ModelOverridePackerEntry
    }

    override fun packAll(entries: List<PackEntry>, packerTargetFolder: File): List<PackResult<Boolean>> {
        val entryList = entries.filterIsInstance<ModelOverridePackerEntry>()
        return entryList.map {
            passPack<ModelPacker, Boolean>(
                ModelPackerEntry(
                    it.toOverride,
                    it.location()
                ), packerTargetFolder
            ).toSuccessResult()
        }
    }
}

class ModelOverridePackerEntry(
    val toOverride: Model,
    // overrideするものを指定する valueは
    val predicate: Map<ModelOverridePredicate, PackLocation>,
) : PackEntry() {
    override fun packEntryName(): String = "[ModelOverride]${toOverride.displayName}"

    override fun location(): PackLocation {
        return toOverride.path
    }
}

/**
 * ResourcePack/models内の、jsonファイルの中の、overrides/predicateの中身
 */
class ModelOverridePredicate(
    val key: String,    // 例えば、"custom_model_data"
    val value: Int  // Should be String | Int
)