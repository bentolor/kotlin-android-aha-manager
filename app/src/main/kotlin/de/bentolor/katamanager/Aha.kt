package de.bentolor.katamanager

import java.util.*

data class Aha(
        val id: UUID = UUID.randomUUID(),
        var title: String = "",
        var date: Date = Date(),
        var isUseful: Boolean = false
) {
    override fun toString(): String {
        return title
    }
}
