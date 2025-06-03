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

/**
 * Manages updating [Variable]s and [Button]s. When [update] is called, all variables and buttons are updated, in that
 * order.
 *
 * Also manages the current layer, which is used to determine which callbacks are run. If the current layer is null,
 * only the global callbacks are run.
 *
 * @author BeepBot99
 * @see button for creating and registering buttons
 * @see variable for creating and registering variables
 * @see range for creating and registering ranges
 */
object BindingManager {

    /**
     * The current layer, or null if there is no current layer.
     */
    var layer: String? = null
    private val variables = mutableListOf<Variable<*>>()
    private val buttons = mutableListOf<Button>()

    /**
     * Updates all variables and buttons.
     *
     * Variables are updated first, and then button. This is so that buttons that depend on variables can use the
     * updated values.
     */
    fun update() {
        variables.forEach { it.update() }
        buttons.forEach { it.update(layer) }
    }

    /**
     * Adds a variable to the list of variables to be updated.
     */
    fun add(variable: Variable<*>) {
        variables.add(variable)
    }

    /**
     * Adds a button to the list of buttons to be updated.
     */
    fun add(button: Button) {
        buttons.add(button)
    }

    /**
     * Resets the manager to its initial state.
     *
     * Removes all variables and buttons and sets the current layer to null.
     */
    fun reset() {
        variables.clear()
        buttons.clear()
        layer = null
    }
}