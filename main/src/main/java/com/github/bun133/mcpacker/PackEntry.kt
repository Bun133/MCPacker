package com.github.bun133.mcpacker

import java.io.File

/**
 * packするファイルの情報を保持するクラス
 */
abstract class PackEntry<out E : PackEntry<E>> {
    /**
     * packするファイルの名前
     *
     * @note ただの表示名なので、正しいファイル名は[location]で返す
     */
    abstract fun packEntryName(): String

    /**
     * pack先の場所の名前
     */
    abstract fun location(): PackLocation<E>
}

/**
 * packするファイルの場所を保持するクラス
 */
abstract class PackLocation<out E : PackEntry<E>> {
    /**
     * packするファイルの場所をStringで返す
     */
    abstract fun pathString(): String

    /**
     * packするファイルの場所をFileで返す
     * @param packerTargetFolder MCPackerに渡される、pack先のフォルダ
     */
    abstract fun file(packerTargetFolder: File): File
}

class InsidePackLocation<E : PackEntry<E>>(
    val rawPathString: String
) : PackLocation<E>() {
    override fun pathString(): String = "[DATAPACKROOT]\\$rawPathString"

    override fun file(packerTargetFolder: File): File {
        return File(packerTargetFolder, rawPathString)
    }
}

/**
 * Packのroot以下のパス文字列からPackLocationを生成する
 */
fun <E : PackEntry<E>> packLocation(pathString: String): InsidePackLocation<E> {
    return InsidePackLocation(pathString)
}