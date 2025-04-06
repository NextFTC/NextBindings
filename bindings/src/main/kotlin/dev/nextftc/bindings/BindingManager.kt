/*
 * NextFTC: a user-friendly control library for FIRST Tech Challenge
 * Copyright (C) 2025 Rowan McAlpin
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package dev.nextftc.bindings

import dev.nextftc.bindings.buttons.Button

internal object BindingManager {
    private val buttons: MutableList<Button> = mutableListOf()
    private val ranges: MutableList<Range> = mutableListOf()

    var layer: String? = null

    fun update() {
        ranges.forEach { it.update() }
        buttons.forEach { it.update(layer) }
    }

    fun add(button: Button) = buttons.add(button)
    fun add(range: Range) = ranges.add(range)

    fun clear() = buttons.clear()
}
