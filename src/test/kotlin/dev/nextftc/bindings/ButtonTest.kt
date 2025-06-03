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
import io.kotest.matchers.should
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.beTheSameInstanceAs
import io.kotest.property.Arb
import io.kotest.property.arbitrary.boolean
import io.kotest.property.arbitrary.list
import io.kotest.property.checkAll
import io.mockk.mockk
import io.mockk.verify

class ButtonTest : DescribeSpec() {
    init {
        describe("get") {
            it("returns the current value") {
                var value = false
                val button = Button { value }
                button.get() shouldBe false
                value = true
                button.update(null)
                button.get() shouldBe true
            }
        }

        describe("whenTrue") {
            it("runs when value is true") {
                checkAll(Arb.list(Arb.boolean(), 0..100)) { values ->
                    var count = 0
                    var current = values.firstOrNull() ?: false
                    val button = Button { current }
                    button whenTrue { count++ }
                    values.forEach { value ->
                        current = value
                        button.update(null)
                    }
                    count shouldBe values.count { it }
                }
            }
        }

        describe("whenFalse") {
            it("runs when value is false") {
                checkAll(Arb.list(Arb.boolean(), 0..100)) { values ->
                    var count = 0
                    var current = values.firstOrNull() ?: true
                    val button = Button { current }
                    button whenFalse { count++ }
                    values.forEach { value ->
                        current = value
                        button.update(null)
                    }
                    count shouldBe values.count { !it }
                }
            }
        }

        describe("whenBecomesTrue") {
            it("runs on rising edge") {
                checkAll(Arb.list(Arb.boolean(), 0..100)) { values ->
                    var count = 0
                    var current = values.firstOrNull() ?: false
                    val button = Button { current }
                    button whenBecomesTrue { count++ }
                    values.drop(1).forEach { value ->
                        current = value
                        button.update(null)
                    }
                    val expected = values
                        .windowed(2)
                        .count { (previous, current) -> !previous && current }
                    count shouldBe expected
                }
            }
        }

        describe("whenBecomesFalse") {
            it("runs on falling edge") {
                checkAll(Arb.list(Arb.boolean(), 0..100)) { values ->
                    var count = 0
                    var current = values.firstOrNull() ?: true
                    val button = Button { current }
                    button whenBecomesFalse { count++ }
                    values.drop(1).forEach { value ->
                        current = value
                        button.update(null)
                    }
                    val expected = values
                        .windowed(2)
                        .count { (previous, current) -> previous && !current }
                    count shouldBe expected
                }
            }
        }

        describe("toggleOnBecomesTrue") {
            it("toggles on rising edge") {
                checkAll(Arb.list(Arb.boolean(), 0..100)) { values ->
                    var value = values.firstOrNull() ?: false
                    val button = Button { value }
                    val toggleButton = button.toggleOnBecomesTrue()
                    var expectedToggle = false
                    values
                        .windowed(2)
                        .forEach { (previous, current) ->
                            val isRising = !previous && current
                            if (isRising) expectedToggle = !expectedToggle
                            value = current
                            button.update(null)
                            toggleButton.update(null)
                            toggleButton.get() shouldBe expectedToggle
                        }
                }
            }
        }

        describe("toggleOnBecomesFalse") {
            it("toggles on falling edge") {
                checkAll(Arb.list(Arb.boolean(), 0..100)) { values ->
                    var value = values.firstOrNull() ?: true
                    val button = Button { value }
                    val toggleButton = button.toggleOnBecomesFalse()
                    var expectedToggle = false
                    values
                        .windowed(2)
                        .forEach { (previous, current) ->
                            val isFalling = previous && !current
                            if (isFalling) expectedToggle = !expectedToggle
                            value = current
                            button.update(null)
                            toggleButton.update(null)
                            toggleButton.get() shouldBe expectedToggle
                        }
                }
            }
        }

        context("LayeredButton") {
            it("creates bindings that are only called when the layer is active") {
                checkAll(Arb.list(Arb.boolean(), 0..100)) { values ->
                    var count = 0
                    var current = values.firstOrNull() ?: false
                    val button = Button { current }
                    button.inLayer("raspberry ice cream") {
                        whenTrue { count++ }
                    }
                    values.forEach { value ->
                        current = value
                        button.update(null)
                    }
                    values.forEach { value ->
                        current = value
                        button.update("raspberry ice cream")
                    }
                    count shouldBe values.count { it }
                }
            }
            describe("global") {
                it("returns the global button") {
                    val button = Button { false }
                    val layeredButton = button.inLayer("raspberry ice cream")
                    layeredButton.global() should beTheSameInstanceAs(button)
                }
            }
            describe("inLayer") {
                it("delegates to the global button") {
                    val button = mockk<Button>(relaxed = true)
                    val layeredButton = button.LayeredButton("raspberry ice cream")
                    layeredButton.inLayer("chocolate pudding")
                    verify(exactly = 1) { button.inLayer("chocolate pudding") }
                }
            }
        }
        describe("and") {
            it("returns a button that is true when both are true") {
                checkAll<Boolean, Boolean> { a, b ->
                    val button1 = Button { a }
                    val button2 = Button { b }
                    val andButton = button1 and button2
                    andButton.get() shouldBe (a && b)
                }
            }
        }
        describe("or") {
            it("returns a button that is true when either is true") {
                checkAll<Boolean, Boolean> { a, b ->
                    val button1 = Button { a }
                    val button2 = Button { b }
                    val orButton = button1 or button2
                    orButton.get() shouldBe (a || b)
                }
            }
        }
        describe("xor") {
            it("returns a button that is true when exactly one is true") {
                checkAll<Boolean, Boolean> { a, b ->
                    val button1 = Button { a }
                    val button2 = Button { b }
                    val xorButton = button1 xor button2
                    xorButton.get() shouldBe (a xor b)
                }
            }
        }
        describe("not") {
            it("returns a button that is the opposite of the original") {
                checkAll<Boolean> { value ->
                    val button = Button { value }
                    val notButton = !button
                    notButton.get() shouldBe !value
                }
            }
        }
    }
}