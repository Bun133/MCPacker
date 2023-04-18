package com.github.bun133.mcpacker.defaultPacker

import com.github.bun133.mcpacker.PackEntry
import com.github.bun133.mcpacker.PackLocation
import com.github.bun133.mcpacker.PackResult
import com.google.gson.GsonBuilder
import java.io.File

/**
 * Modelのjsonファイルをpackするやつ
 */
class ModelPacker : DefaultPacker<Boolean>() {
    override fun abstractInit() {
        // DO NOTHING
    }

    override fun isSupported(entry: PackEntry): Boolean {
        return entry is ModelPackerEntry
    }

    override fun packAll(entries: List<PackEntry>, packerTargetFolder: File): List<PackResult<Boolean>> {
        val entryList = entries.filterIsInstance<ModelPackerEntry>()
        return entryList.map {
            val packerTarget = it.location().file(packerTargetFolder)
            packerTarget.parentFile.mkdirs()
            packerTarget.writeText(it.model.toJson())
            PackResult(true)
        }
    }
}

class ModelPackerEntry(
    val model: Model,
    val path: PackLocation
) : PackEntry() {
    override fun packEntryName(): String = "[Model]${model.displayName}"
    override fun location(): PackLocation = path
}

/**
 * Modelのjsonファイルの中身
 */
data class Model(
    val path: PackLocation,
    // MCPacker内の名前
    val displayName: String,
    // parentフィールド (minecraft:item/generated とか)
    val parent: String,
    // texturesフィールド ("layer0": "minecraft:item/iron_sword" とか)
    val textures: Map<String, String>,
    // その他のフィールド
    // TODO あとで実装する
    val fields: Map<String, Any>,
) {
    fun toJson(): String {
        val map = mutableMapOf<String, Any>()
        map["parent"] = parent
        map["textures"] = textures
        map.putAll(fields)
        return GsonBuilder().setPrettyPrinting().create().toJson(map)
    }
}