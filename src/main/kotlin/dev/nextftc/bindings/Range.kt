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
import kotlin.math.abs

/**
 * A special type of [Variable] that has number-specific utilities.
 *
 * @param valueSupplier a supplier that supplies the value of the variable.
 *
 * @author BeepBot99
 * @see Variable for non-double variables
 * @see range for creating a [Range] and registering it with the [BindingManager].
 */
class Range(valueSupplier: Supplier<Double>) : Variable<Double>(valueSupplier) {

    /**
     * Equivalent to [mapToRange].
     */
    fun map(mapper: (Double) -> Double) = mapToRange(mapper)

    /**
     * Returns a new [Range] whose value is the opposite of this [Range].
     */
    fun negate() = map { -it }

    /**
     * Equivalent to [negate].
     */
    operator fun unaryMinus() = negate()

    /**
     * Returns a new [Range] whose value is one minus the value of this [Range].
     */
    fun invert() = map { 1 - it }

    /**
     * Returns a new range whose value is the same as the value of this range, but 0 when the absolute value of this
     * range is less than the provided [threshold].
     */
    fun deadZone(threshold: Double): Range {
        require(threshold >= 0) { "Threshold must be non-negative" }
        return map { if (abs(it) < threshold) 0.0 else it }
    }

    /**
     * Creates a [Button] that is true when the value of this range is less than the given [value] and registers it
     * with the [BindingManager].
     */
    infix fun lessThan(value: Double) = asButton { it < value }

    /**
     * Creates a [Button] that is true when the value of this range is greater than the given [value] and
     * registers it with the [BindingManager].
     */
    infix fun greaterThan(value: Double) = asButton { it > value }

    /**
     * Creates a [Button] that is true when the value of this range is greater than or equal to the given [value] and
     * registers it with the [BindingManager].
     */
    infix fun atLeast(value: Double) = asButton { it >= value }

    /**
     * Creates a [Button] that is true when the value of this range is less than or equal to the given [value] and
     * registers it with the [BindingManager].
     */
    infix fun atMost(value: Double) = asButton { it <= value }

    /**
     * Creates a [Button] that is true when the value of this range is in the given [range] and registers it with
     * the [BindingManager]
     */
    infix fun inRange(range: ClosedFloatingPointRange<Double>) = asButton { it in range }

    /**
     * Creates a [Button] that is true when the value of this range is between [lower] and [upper], inclusive, and
     * registers it with the [BindingManager]
     */
    fun inRange(lower: Double, upper: Double) = inRange(lower..upper)
}