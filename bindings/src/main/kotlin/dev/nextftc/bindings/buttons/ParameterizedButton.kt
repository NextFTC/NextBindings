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

import java.util.function.BiConsumer
import java.util.function.Consumer
import java.util.function.Supplier

class ParameterizedButton<T : Any>(private val parameterSupplier: Supplier<T>, stateSupplier: Supplier<Boolean>) :
    GlobalButton(stateSupplier) {
    private lateinit var parameter: T

    infix fun isPressed(callback: Consumer<T>) = isPressed { -> callback.accept(parameter) }
    infix fun isReleased(callback: Consumer<T>) = isReleased { -> callback.accept(parameter) }
    infix fun isHeld(callback: Consumer<T>) = isHeld { -> callback.accept(parameter) }
    infix fun isNotHeld(callback: Consumer<T>) = isNotHeld { -> callback.accept(parameter) }
    infix fun isToggled(callback: BiConsumer<T, Boolean>) = isToggled { callback.accept(parameter, it) }
    infix fun isToggledFalling(callback: BiConsumer<T, Boolean>) = isToggledFalling { callback.accept(parameter, it) }
    infix fun stateChanges(callback: BiConsumer<T, Boolean>) = stateChanges { callback.accept(parameter, it) }
    infix fun exists(callback: BiConsumer<T, Boolean>) = exists { callback.accept(parameter, it) }

    override fun globalLayer(): ParameterizedButton<T> = this

    @Suppress("UNCHECKED_CAST")
    override infix fun inLayer(layer: String): ParameterizedLayeredButton<T> =
        layers.getOrPut(layer) {
            ParameterizedLayeredButton(
                this,
                layer,
                parameterSupplier
            )
        } as ParameterizedLayeredButton<T>

    override fun update(layer: String?) {
        parameter = parameterSupplier.get()
        super.update(layer)
    }
}

class ParameterizedLayeredButton<T : Any>(
    globalButton: GlobalButton,
    layer: String,
    private val parameterSupplier: Supplier<T>
) : LayeredButton(globalButton, layer) {
    private lateinit var parameter: T

    infix fun isPressed(callback: Consumer<T>) = isPressed { -> callback.accept(parameter) }
    infix fun isReleased(callback: Consumer<T>) = isReleased { -> callback.accept(parameter) }
    infix fun isHeld(callback: Consumer<T>) = isHeld { -> callback.accept(parameter) }
    infix fun isNotHeld(callback: Consumer<T>) = isNotHeld { -> callback.accept(parameter) }
    infix fun isToggled(callback: BiConsumer<T, Boolean>) = isToggled { callback.accept(parameter, it) }
    infix fun isToggledFalling(callback: BiConsumer<T, Boolean>) = isToggledFalling { callback.accept(parameter, it) }
    infix fun stateChanges(callback: BiConsumer<T, Boolean>) = stateChanges { callback.accept(parameter, it) }
    infix fun exists(callback: BiConsumer<T, Boolean>) = exists { callback.accept(parameter, it) }

    override fun update(layer: String?) {
        parameter = parameterSupplier.get()
        super.update(layer)
    }
}