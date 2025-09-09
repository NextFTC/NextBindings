/*
 * NextBindings: a user-friendly bindings library for the FIRST Tech Challenge
 * Copyright (C) 2025 NextFTC
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

import java.util.function.Supplier

/**
 * A button that is either pressed or not pressed. Can either represent a physical button or any boolean state.
 * Supports binding callbacks to certain events, which are called when [update] is called, usually through
 * [BindingManager.update].
 *
 * Events that allow binding to are:
 *   - [whenBecomesTrue], which triggers on the rising edge
 *   - [whenBecomesFalse], which triggers on the falling edge
 *   - [whenTrue], which triggers whenever the button is pressed
 *   - [whenFalse], which triggers whenever the button is not pressed
 *
 * Additionally, [toggleOnBecomesTrue] and [toggleOnBecomesFalse] return a new button with a state that toggles on
 * the rising and falling edges of the button, respectively.
 *
 * The [inLayer] function allows binding callbacks to a specific layer. When the button is pressed and the specified
 * layer is active, the callbacks are triggered. When the button is pressed and the active layer is a different layer,
 * or if there is no active layer, the callbacks are not triggered.
 *
 * The [and], [or], and [xor] functions can be used to combine multiple buttons into a single new button. The [not]
 * function creates a button whose state is the opposite of the button.
 *
 * @param valueSupplier a [Supplier] that returns the current state of the button.
 *
 * @author BeepBot99
 * @see button for creating and registering buttons in [BindingManager]
 */
class Button(private val valueSupplier: Supplier<Boolean>) : Supplier<Boolean> {
    private class Callback {
        private val callbacks = mutableListOf<Pair<Layer, Runnable>>()

        fun run(currentLayer: String?) = callbacks.forEach {
            val layer = it.first
            when (layer) {
                is Layer.Global -> it.second.run()
                is Layer.Named -> if (layer.name == currentLayer) it.second.run()
            }
        }

        fun add(layer: Layer, runnable: Runnable) {
            callbacks.add(layer to runnable)
        }

        fun clear() = callbacks.clear()
    }

    private sealed interface Layer {
        object Global : Layer
        class Named(val name: String?) : Layer
    }

    private var value: Boolean = false
    private var previousValue = value

    /**
     * Gets the cached current state of the button. The cached state is updated when [update] is called.
     */

    override fun get(): Boolean = value

    private val risingEdgeCallback = Callback()
    private val fallingEdgeCallback = Callback()
    private val trueCallback = Callback()
    private val falseCallback = Callback()

    /**
     * Adds a callback to be triggered on the rising edge of the button.
     */
    infix fun whenBecomesTrue(callback: Runnable) = apply { risingEdgeCallback.add(Layer.Global, callback) }

    /**
     * Adds a callback to be triggered on the falling edge of the button.
     */
    infix fun whenBecomesFalse(callback: Runnable) = apply { fallingEdgeCallback.add(Layer.Global, callback) }

    /**
     * Adds a callback to be triggered whenever the button is pressed.
     */
    infix fun whenTrue(callback: Runnable) = apply { trueCallback.add(Layer.Global, callback) }

    /**
     * Adds a callback to be triggered whenever the button is not pressed.
     */
    infix fun whenFalse(callback: Runnable) = apply { falseCallback.add(Layer.Global, callback) }

    /**
     * Returns a new button with a state that toggles on the rising edge of the button.
     */
    fun toggleOnBecomesTrue(): Button {
        var toggleState = false
        whenBecomesTrue { toggleState = !toggleState }
        return button { toggleState }
    }

    /**
     * Returns a new button with a state that toggles on the falling edge of the button.
     */
    fun toggleOnBecomesFalse(): Button {
        var toggleState = false
        whenBecomesFalse { toggleState = !toggleState }
        return button { toggleState }
    }

    /**
     * A button that is bound to a specific layer. When the button is pressed and the specified layer is active, the
     * callbacks are triggered. When the button is pressed and the active layer is a different layer, or if there is no
     * active layer, the callbacks are not triggered.
     *
     * @param layer the name of the layer to bind the button to, or null to bind to the button when no layer is active.
     *
     * @see inLayer for binding callbacks to a specific layer
     * @see BindingManager.layer for setting the active layer
     */
    inner class LayeredButton internal constructor(layer: String?) {
        private val layer = Layer.Named(layer)

        /**
         * Adds a callback to be triggered on the rising edge of the button when the specified layer is active.
         */
        fun whenBecomesTrue(callback: Runnable) = apply { risingEdgeCallback.add(layer, callback) }

        /**
         * Adds a callback to be triggered on the falling edge of the button when the specified layer is active.
         */
        fun whenBecomesFalse(callback: Runnable) = apply { fallingEdgeCallback.add(layer, callback) }

        /**
         * Adds a callback to be triggered whenever the button is pressed when the specified layer is active.
         */
        fun whenTrue(callback: Runnable) = apply { trueCallback.add(layer, callback) }

        /**
         * Adds a callback to be triggered whenever the button is not pressed when the specified layer is active.
         */
        fun whenFalse(callback: Runnable) = apply { falseCallback.add(layer, callback) }

        /**
         * Calls [Button.inLayer] with the specified layer and returns the result.
         */
        fun inLayer(layer: String) = this@Button.inLayer(layer)

        /**
         * Returns the global [Button]
         */
        fun global() = this@Button
    }

    /**
     * Returns a [LayeredButton] that is bound to the specified layer. Bindings made from the returned [LayeredButton]
     * are only triggered when the specified layer is active.
     */
    fun inLayer(layer: String?) = LayeredButton(layer)

    /**
     * Executes the provided block of code within the context of a specific layer, represented by a [LayeredButton].
     * The callbacks defined inside this block will be triggered only when the specified layer is active.
     */
    @JvmSynthetic
    fun inLayer(layer: String?, block: LayeredButton.() -> Unit) = apply { block(inLayer(layer)) }

    /**
     * Updates the button's state and triggers the appropriate callbacks.
     *
     * @param layer the active layer, or null if there is no active layer.
     */
    fun update(layer: String?) {
        value = valueSupplier.get()

        if (value && !previousValue) risingEdgeCallback.run(layer)
        if (!value && previousValue) fallingEdgeCallback.run(layer)
        if (value) trueCallback.run(layer)
        if (!value) falseCallback.run(layer)

        previousValue = value
    }

    /**
     * Returns a new button whose state is the logical and of the states of this button and the specified button.
     */
    infix fun and(supplier: Supplier<Boolean>) = button { get() and supplier.get() }

    /**
     * Returns a new button whose state is the logical or of the states of this button and the specified button.
     */
    infix fun or(supplier: Supplier<Boolean>) = button { get() or supplier.get() }

    /**
     * Returns a new button whose state is the logical xor of the states of this button and the specified button.
     */
    infix fun xor(supplier: Supplier<Boolean>) = button { get() xor supplier.get() }

    /**
     * Returns a new button whose state is the logical not of the state of this button.
     */
    operator fun not() = button { !get() }

    internal fun clear() {
        risingEdgeCallback.clear()
        fallingEdgeCallback.clear()
        trueCallback.clear()
        falseCallback.clear()
    }
}