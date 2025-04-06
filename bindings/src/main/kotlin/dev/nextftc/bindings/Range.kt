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

import dev.nextftc.bindings.buttons.GlobalButton
import dev.nextftc.bindings.buttons.ParameterizedButton
import java.util.function.Predicate
import java.util.function.Supplier

class Range(val valueSupplier: Supplier<Double>) : Supplier<Double> {
    init {
        BindingManager.add(this)
    }

    private var currentValue = 0.0
    override fun get(): Double = currentValue

    infix fun whenTrue(condition: Predicate<Double>): ParameterizedButton<Double> =
        ParameterizedButton(this) { condition.test(get()) }

    infix fun whenFalse(condition: Predicate<Double>): ParameterizedButton<Double> =
        ParameterizedButton(this) { !condition.test(get()) }

    infix fun whenInRange(range: ClosedRange<Double>) = whenTrue { it in range }
    fun whenInRange(start: Double, end: Double) = whenInRange(start..end)
    infix fun whenOutsideRange(range: ClosedRange<Double>) = whenFalse { it in range }
    fun whenOutsideRange(start: Double, end: Double) = whenOutsideRange(start..end)

    infix fun whenGreaterThan(lowerBound: Double) = whenTrue { it > lowerBound }
    infix fun whenGreaterThanOrEqualTo(lowerBound: Double) = whenTrue { it >= lowerBound }
    infix fun whenLessThan(upperBound: Double) = whenTrue { it < upperBound }
    infix fun whenLessThanOrEqualTo(upperBound: Double) = whenTrue { it <= upperBound }

    fun map(map: (Double) -> Double) = Range { map(get()) }

    fun negate(): Range = map { -it }
    fun invert(): Range = map { 1.0 - it }

    fun update() {
        currentValue = valueSupplier.get()
    }
}
