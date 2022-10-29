package com.github.bun133.mcpacker

import java.io.File

/**
 * 実際にファイルをpackするクラス
 */
abstract class Packer<R : Any> {
    fun init(mcPacker: MCPacker) {
        this.mcPacker = mcPacker
    }

    private lateinit var mcPacker: MCPacker

    /**
     * 他の特定のPackerに[packEntry]のpackを丸投げする
     *
     * @param T 丸投げ先のPackerの型
     */
    internal inline fun <reified T : Packer<R>, R : Any> passPack(
        packEntry: PackEntry<*>,
        packerTargetFolder: File
    ): PackResult<R> {
        return mcPacker.passPack<T, R>(packEntry, packerTargetFolder)
    }

    /**
     * 他のPackerに[packEntry]のpackを丸投げする
     */
    @JvmName("passPack1")
    internal fun passPack(packEntry: PackEntry<*>, packerTargetFolder: File): PackResult<*> {
        return mcPacker.passPack(packEntry, packerTargetFolder)
    }

    /**
     * Packerの初期化処理
     * 一種類のPackerにつき一度だけ呼ばれ、インスタンスは必ず一つであることが保証される
     */
    abstract fun abstractInit()

    /**
     * [entry]をpackできるかどうかを返す
     */
    abstract fun isSupported(entry: PackEntry<*>): Boolean

    /**
     * すべて一括でpackする
     *
     * @param packerTargetFolder MCPackerに渡される、pack先のフォルダ
     * @return packの成功可否
     */
    internal abstract fun packAll(
        entries: List<PackEntry<*>>,
        packerTargetFolder: File
    ): List<PackResult<R>>
}

class PackResult<R : Any>(val data: R?, val error: Throwable? = null) {
    fun isSuccess(): Boolean {
        return data != null
    }

    fun isError(): Boolean {
        return error != null
    }
}

/**
 * 失敗成功を表す[PackResult]を返す
 */
fun PackResult<*>.toSuccessResult(): PackResult<Boolean> {
    return if (isError()) {
        PackResult(null, error)
    } else {
        PackResult(true, null)
    }
}