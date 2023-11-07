package com.github.texousliu.opengitmoji.model

class RegexTableInfo(val rows : MutableList<RegexTableRowInfo>) {

    fun add(row : RegexTableRowInfo) : Boolean {
        return rows.add(row)
    }

    fun remove(row : RegexTableRowInfo) : Boolean {
        return rows.remove(row)
    }

    fun removeAt(index : Int) : RegexTableRowInfo? {
        if (index < 0) return null
        return rows.removeAt(index)
    }

    fun reset(info : RegexTableInfo) {
        rows.clear()
        rows.addAll(info.rows)
    }

}