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

package dev.nextftc.bindings.buttons

import dev.nextftc.bindings.BindingManager

open class LayeredButton(val globalButton: GlobalButton, val layer: String) : Button(globalButton::get) {
    override fun inLayer(layer: String): LayeredButton = globalButton.inLayer(layer)

    override fun globalLayer(): GlobalButton = globalButton
    override fun asGlobal(): GlobalButton = GlobalButton { globalButton.get() && BindingManager.layer == layer }
}