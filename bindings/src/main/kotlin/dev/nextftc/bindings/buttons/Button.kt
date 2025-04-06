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

import java.util.function.Consumer
import java.util.function.Supplier

abstract class Button(val stateSupplier: Supplier<Boolean>) : Supplier<Boolean> {

    @get:JvmName("currentState")
    var currentState = false
        private set
    private var risingEdgeToggle = false
    private var fallingEdgeToggle = false
    override fun get(): Boolean = currentState

    private val pressedCallback = Callback.empty<Unit>()
    private val heldCallback = Callback.empty<Unit>()
    private val releasedCallback = Callback.empty<Unit>()
    private val notHeldCallback = Callback.empty<Unit>()
    private val alwaysCallback = Callback.empty<Boolean>()
    private val toggledCallbackRising = Callback.empty<Boolean>()
    private val toggledCallbackFalling = Callback.empty<Boolean>()
    private val stateChangedCallback = Callback.empty<Boolean>()

    infix fun isPressed(callback: Runnable) = apply { pressedCallback += callback }
    infix fun isHeld(callback: Runnable) = apply { heldCallback += callback }
    infix fun isReleased(callback: Runnable) = apply { releasedCallback += callback }
    infix fun isNotHeld(callback: Runnable) = apply { notHeldCallback += callback }
    infix fun isToggled(callback: Consumer<Boolean>) = apply { toggledCallbackRising += callback }
    infix fun isToggledFalling(callback: Consumer<Boolean>) = apply { toggledCallbackFalling += callback }
    infix fun stateChanges(callback: Consumer<Boolean>) = apply { stateChangedCallback += callback }
    infix fun exists(callback: Consumer<Boolean>) = apply { alwaysCallback += callback }

    open fun update(layer: String?) {
        val previousState = currentState
        currentState = stateSupplier.get()

        alwaysCallback(currentState)

        if (currentState and !previousState) {
            pressedCallback()
            risingEdgeToggle = !risingEdgeToggle
            toggledCallbackRising(risingEdgeToggle)
        }
        if (!currentState and previousState) {
            releasedCallback()
            fallingEdgeToggle = !fallingEdgeToggle
            toggledCallbackFalling(fallingEdgeToggle)
        }
        if (currentState != previousState) stateChangedCallback(currentState)
        if (currentState) heldCallback()
        if (!currentState) notHeldCallback()
    }

    abstract infix fun inLayer(layer: String): Button
    abstract fun globalLayer(): Button
    abstract fun asGlobal(): GlobalButton

    infix fun and(other: Button): GlobalButton {
        val global = this.asGlobal()
        val otherGlobal = other.asGlobal()
        return GlobalButton { global.get() && otherGlobal.get() }
    }

    infix fun or(other: Button): GlobalButton {
        val global = this.asGlobal()
        val otherGlobal = other.asGlobal()
        return GlobalButton { global.get() || otherGlobal.get() }
    }

    infix fun xor(other: Button): GlobalButton {
        val global = this.asGlobal()
        val otherGlobal = other.asGlobal()
        return GlobalButton { global.get() xor otherGlobal.get() }
    }

    operator fun not(): GlobalButton {
        val global = this.asGlobal()
        return GlobalButton { !global.get() }
    }

    infix fun heldFor(debounceTimeMillis: Long): GlobalButton = GlobalButton(this.asGlobal())

    fun <T : Any> parameterize(parameterSupplier: Supplier<T>): ParameterizedButton<T> =
        ParameterizedButton(parameterSupplier, this.asGlobal())
}

