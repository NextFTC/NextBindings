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

@file:JvmName("Bindings")

package dev.nextftc.bindings

import dev.nextftc.bindings.buttons.Button
import dev.nextftc.bindings.buttons.DebouncingSupplier
import dev.nextftc.bindings.buttons.GlobalButton
import java.util.function.Supplier

fun updateBindings() = BindingManager.update()

fun disposeBindings() = BindingManager.clear()

var layer by BindingManager::layer

fun whenButton(stateSupplier: Supplier<Boolean>) = GlobalButton(stateSupplier)
fun whenButton(debounceTimeMillis: Long, stateSupplier: Supplier<Boolean>) =
    GlobalButton(DebouncingSupplier(debounceTimeMillis, stateSupplier))

fun whenButton(button: Button) = button

fun range(valueSupplier: Supplier<out Number>) = Range { valueSupplier.get().toDouble() }