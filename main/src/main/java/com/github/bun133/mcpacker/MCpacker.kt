package com.github.bun133.mcpacker

import com.github.bun133.mcpacker.defaultPacker.BlockTexturePacker
import com.github.bun133.mcpacker.defaultPacker.DataPackPacker
import com.github.bun133.mcpacker.defaultPacker.ImagePacker
import com.github.bun133.mcpacker.defaultPacker.ItemTexturePacker
import java.io.File


class MCPacker {
    private val entries: MutableList<PackEntry> = mutableListOf()
    private val packerRegistry = PackerRegistry()
    private var isInited = false

    private fun init() {
        if (isInited) return
        initDefaultPacker()
        isInited = true
    }

    /**
     * packするファイルを追加する
     */
    fun addPackEntry(entry: PackEntry) {
        entries.add(entry)
    }

    /**
     * Packerを追加する
     * @note [f] の中身はただインスタンスを作るだけで、packerの初期化は行わない
     */
    fun registerPacker(f: () -> Packer<*>) {
        val packer = f()
        packer.init(this)
        packerRegistry.addPacker(packer)
    }

    /**
     * 実際にpackした結果を表示・返却する
     */
    fun generate(packerTargetFolder: File): Boolean {
        if (!isInited) init()   // InitされてなければInitする

        // packするファイルをすべてpackする
        val result = actualGenerate(packerTargetFolder)

        // エラーがあったらエラーを出力する
        val errored = result.toList().filter { it.second.isError() }
        val isSuccess = errored.isEmpty()

        errored.forEachIndexed { index, (entry, packResult) ->
            println("====== Packing Error ${index + 1}/${errored.size} ======")
            println("Error: ${entry.packEntryName()}")
            packResult.error?.printStackTrace()
            println("====== End of Error ${index + 1}/${errored.size} ======")
        }

        return isSuccess
    }

    private fun actualGenerate(packerTargetFolder: File): MutableMap<PackEntry, PackResult<*>> {
        val packerResults = mutableMapOf<PackEntry, PackResult<*>>()

        val entryToPacker = entries
            .map { it to packerRegistry.getSupportedPacker(it).firstOrNull() } // TODO: ここで複数のPackerがあった場合の処理

        // Packerがないものはエラーを返す
        val nullPacker = entryToPacker.filter { it.second == null }
        nullPacker.forEach {
            packerResults[it.first] = PackResult(null, Throwable("No Packer for PackEntry:${it.first.packEntryName()}"))
        }

        // Packerがあるものはpackする
        val notNullPacker = entryToPacker.filter { it.second != null }
        val packerToEntry = notNullPacker.groupBy { it.second!! }
        packerToEntry.forEach { (packer, entryList) ->
            // packerを使ってpackする
            val packerResult = packer.packAll(entryList.map { it.first }, packerTargetFolder)
            // packerResultをpackerResultsに追加する
            packerResult.forEachIndexed { index, result ->
                packerResults[entryList[index].first] = result
            }
        }

        return packerResults
    }

    /**
     * デフォルトのPackerを登録する
     */
    private fun initDefaultPacker() {
        registerPacker { DataPackPacker() }
        registerPacker { ImagePacker() }
        registerPacker { BlockTexturePacker() }
        registerPacker { ItemTexturePacker() }
    }

    /**
     * 他の特定のPackerにPack処理を丸投げするためのfunction
     *
     * @param T 他のPackerの型
     */
    internal inline fun <reified T : Packer<R>, R : Any> passPack(
        entry: PackEntry,
        packerTargetFolder: File
    ): PackResult<R> {
        val packer = packerRegistry.getSupportedPacker(entry).filterIsInstance<T>().firstOrNull()
        return packer?.packAll(listOf(entry), packerTargetFolder)?.first() ?: PackResult<R>(
            null,
            Throwable("No Packer for PackEntry:${entry.packEntryName()}")
        )
    }

    /**
     * 他のPackerが処理しているエントリを他のPackerに丸投げするためのfunction
     */
    @JvmName("passPack1")
    internal fun passPack(entry: PackEntry, packerTargetFolder: File): PackResult<*> {
        val packer = packerRegistry.getSupportedPacker(entry).firstOrNull()
        return packer?.packAll(listOf(entry), packerTargetFolder)?.first() ?: PackResult(
            null,
            Throwable("No Packer for PackEntry:${entry.packEntryName()}")
        )
    }
}

/**
 * すべてをpackします
 * @param packerTargetFolder pack先のフォルダ
 * @return packの成功可否
 */
fun pack(packerTargetFolder: File, entries: List<PackEntry>): Boolean {
    val mcpacker = MCPacker()
    entries.forEach { mcpacker.addPackEntry(it) }
    return mcpacker.generate(packerTargetFolder)
}