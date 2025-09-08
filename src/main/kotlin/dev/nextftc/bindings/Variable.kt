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

import java.util.function.Predicate
import java.util.function.Supplier

/**
 * A variable that can be updated, mapped, and used as a button.
 *
 * @param T the type of the variable.
 * @param valueSupplier a supplier that supplies the value of the variable.
 *
 * @author BeepBot99
 * @see variable for creating a variable.
 * @see Range for creating a double variable with number-specific utilities.
 */
open class Variable<T>(private val valueSupplier: Supplier<T>) : Supplier<T> {
    private var value: T? = null

    /**
     * Gets the cached value of the variable. The cached value is updated when [update] is called.
     */
    override fun get(): T = value ?: error("Variable not updated; call update() first")

    /**
     * Creates a new [Variable] that maps the value of this variable using the given [mapper] and registers it with
     * the [BindingManager].
     */
    fun <R> map(mapper: (T) -> R) = variable { mapper(get()) }

    /**
     * Same as [map], but returns a [Range] instead of a [Variable]. Because of this, it only works when mapping to a
     * double.
     */
    fun mapToRange(mapper: (T) -> Double) = range { mapper(get()) }

    /**
     * Creates a [Button] that is true when [predicate] is true for the value of this variable and registers it with
     * the [BindingManager].
     */
    fun asButton(predicate: Predicate<T>) = button { predicate.test(get()) }

    /**
     * Updates the cached value of the variable. Usually called through [BindingManager.update].
     */
    fun update() {
        value = valueSupplier.get()
    }
}