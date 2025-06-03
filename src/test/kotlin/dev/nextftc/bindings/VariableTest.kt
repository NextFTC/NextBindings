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

import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import io.kotest.property.checkAll

class VariableTest : DescribeSpec() {
    init {
        describe("get") {
            it("returns the current value") {
                checkAll<Double, Double> { firstValue, secondValue ->
                    var value = firstValue
                    val variable = Variable { value }
                    variable.get() shouldBe firstValue
                    value = secondValue
                    variable.update()
                    variable.get() shouldBe secondValue
                }
            }
        }
        describe("map") {
            it("transforms the value using the provided function") {
                checkAll<String> { value ->
                    val variable = Variable { value }
                    val mapped = variable.map { it + it }
                    mapped.get() shouldBe value + value
                }
            }
            it("propagates value changes") {
                checkAll<String, String> { firstValue, secondValue ->
                    var value = firstValue
                    val variable = Variable { value }
                    val mapped = variable.map { it + it }
                    mapped.get() shouldBe firstValue + firstValue
                    value = secondValue
                    variable.update()
                    mapped.update()
                    mapped.get() shouldBe secondValue + secondValue
                }
            }
        }
        describe("mapToRange") {
            it("creates a Range with the transformed value") {
                checkAll<String> { value ->
                    val variable = Variable { value }
                    val range = variable.mapToRange { it.length.toDouble() }
                    range.get() shouldBe value.length.toDouble()
                }
            }
            it("propagates value changes") {
                checkAll<Int, Int> { firstValue, secondValue ->
                    var value = firstValue
                    val variable = Variable { value }
                    val range = variable.mapToRange { (it * it).toDouble() }
                    range.get() shouldBe (firstValue * firstValue).toDouble()
                    value = secondValue
                    variable.update()
                    range.update()
                    range.get() shouldBe (secondValue * secondValue).toDouble()
                }
            }
        }
        describe("asButton") {
            it("creates a Button based on the predicate") {
                checkAll<Int, Int> { value, greaterThan ->
                    val variable = Variable { value }
                    val button = variable.asButton { it > greaterThan }
                    button.get() shouldBe (value > greaterThan)
                }
            }
            it("propagates value changes") {
                checkAll<Int, Int, Int> { firstValue, greaterThan, secondValue ->
                    var value = firstValue
                    val variable = Variable { value }
                    val button = variable.asButton { it > greaterThan }
                    button.get() shouldBe (value > greaterThan)
                    value = secondValue
                    variable.update()
                    button.update(null)
                    button.get() shouldBe (value > greaterThan)
                    value = greaterThan
                    variable.update()
                    button.update(null)
                }
            }
        }
    }
}