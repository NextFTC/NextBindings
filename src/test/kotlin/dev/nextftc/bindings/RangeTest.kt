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

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.arbitrary.double
import io.kotest.property.arbitrary.flatMap
import io.kotest.property.arbitrary.map
import io.kotest.property.arbitrary.negativeDouble
import io.kotest.property.arbitrary.positiveDouble
import io.kotest.property.assume
import io.kotest.property.checkAll
import kotlin.math.abs

class RangeTest : DescribeSpec() {
    init {

        describe("map") {
            it("transforms the value using the provided function") {
                checkAll<Double> { value ->
                    val range = Range { value }
                    val mapped = range.map { it * 2 }
                    mapped.get() shouldBe value * 2
                }
            }

            it("propagates value changes") {
                checkAll<Double, Double> { firstValue, secondValue ->
                    var value = firstValue
                    val range = Range { value }
                    val mapped = range.map { it * 2 }
                    mapped.get() shouldBe firstValue * 2
                    value = secondValue
                    range.update()
                    mapped.update()
                    mapped.get() shouldBe secondValue * 2
                }
            }
        }

        fun testNegate(name: String, negate: (Range) -> Range) {
            describe(name) {
                it("returns the negated value") {
                    checkAll<Double> { value ->
                        val range = Range { value }
                        val negated = negate(range)
                        negated.get() shouldBe -value
                    }
                }
            }
        }

        testNegate("negate", Range::negate)
        testNegate("unaryMinus", Range::unaryMinus)

        describe("invert") {
            it("returns 1 minus the value") {
                checkAll<Double> { value ->
                    val range = Range { value }
                    val inverted = range.invert()
                    inverted.get() shouldBe 1 - value
                }
            }
        }

        describe("deadZone") {
            it("returns 0 when abs(value) is below threshold") {
                val valueThresholdPair = Arb.positiveDouble().flatMap { threshold ->
                    Arb.double(0.0, threshold, false).map { value -> value to threshold }
                }

                checkAll(valueThresholdPair) { (value, threshold) ->
                    assume(abs(value) != abs(threshold))
                    val range = Range { value }
                    val deadZoned = range.deadZone(threshold)
                    deadZoned.get() shouldBe 0.0
                }
            }
            it("returns original value when abs(value) is above threshold") {
                val valueThresholdPair = Arb.positiveDouble().flatMap { threshold ->
                    Arb.double(min = threshold).map { value -> value to threshold }
                }

                checkAll(valueThresholdPair) { (value, threshold) ->
                    assume(abs(value) >= threshold)
                    val range = Range { value }
                    val deadZoned = range.deadZone(threshold)
                    deadZoned.get() shouldBe value
                }
            }
            it("throws when the threshold is negative") {
                checkAll(Arb.double(), Arb.negativeDouble()) { value, threshold ->
                    val range = Range { value }
                    shouldThrow<IllegalArgumentException> { range.deadZone(threshold) }
                }
            }
        }

        describe("lessThan") {
            it("returns a Button that is true when value is less than the threshold") {
                checkAll<Double, Double> { value, threshold ->
                    val range = Range { value }
                    val button = range lessThan threshold
                    button.get() shouldBe (value < threshold)
                }
            }
        }

        describe("greaterThan") {
            it("returns a Button that is true when value is greater than the threshold") {
                checkAll<Double, Double> { value, threshold ->
                    val range = Range { value }
                    val button = range greaterThan threshold
                    button.get() shouldBe (value > threshold)
                }
            }
        }

        describe("atLeast") {
            it("returns a Button that is true when value is greater than or equal to the threshold") {
                checkAll<Double, Double> { value, threshold ->
                    val range = Range { value }
                    val button = range atLeast threshold
                    button.get() shouldBe (value >= threshold)
                }
            }
        }

        describe("atMost") {
            it("returns a Button that is true when value is less than or equal to the threshold") {
                checkAll<Double, Double> { value, threshold ->
                    val range = Range { value }
                    val button = range atMost threshold
                    button.get() shouldBe (value <= threshold)
                }
            }
        }

        describe("inRange") {
            it("returns a Button that is true when value is in the range") {
                checkAll<Double, Double, Double> { value, min, max ->
                    val range = Range { value }
                    val button = range inRange min..max
                    button.get() shouldBe (value in min..max)
                }
            }

            it("can accept bounds as parameters") {
                checkAll<Double, Double, Double> { value, min, max ->
                    val range = Range { value }
                    val button = range.inRange(min, max)
                    button.get() shouldBe (value in min..max)
                }
            }
        }
    }
}