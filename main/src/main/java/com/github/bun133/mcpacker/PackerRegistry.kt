package com.github.bun133.mcpacker

class PackerRegistry {
    private val packers: MutableList<Packer<*>> = mutableListOf()

    /**
     * [packer]を登録する
     */
    internal fun addPacker(packer: Packer<*>) {
        packers.add(packer)
    }

    /**
     * [entry]をpackできるpackerを返す
     */
    internal fun getSupportedPacker(entry: PackEntry<*>): List<Packer<*>> {
        return packers.filter { it.isSupported(entry) }
    }
}