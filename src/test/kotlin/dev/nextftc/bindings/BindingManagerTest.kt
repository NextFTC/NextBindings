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
import io.kotest.matchers.nulls.shouldBeNull
import io.mockk.mockk
import io.mockk.verify

class BindingManagerTest : DescribeSpec() {
    init {
        beforeTest { BindingManager.reset() }
        describe("layer") {
            it("starts as null") {
                BindingManager.layer.shouldBeNull()
            }
        }

        describe("update") {
            it("updates all variables") {
                val variable = mockk<Variable<Int>>(relaxed = true)
                BindingManager.add(variable)
                BindingManager.update()
                verify(exactly = 1) { variable.update() }
            }
            it("updates all buttons") {
                val button = mockk<Button>(relaxed = true)
                BindingManager.add(button)
                BindingManager.update()
                verify(exactly = 1) { button.update(any()) }
            }
            it("passes the current layer to all buttons") {
                val button = mockk<Button>(relaxed = true)
                BindingManager.add(button)
                BindingManager.layer = "testLayer"
                BindingManager.update()
                verify(exactly = 1) { button.update("testLayer") }
            }
            it("doesn't update anything after reset") {
                val variable = mockk<Variable<Int>>(relaxed = true)
                val button = mockk<Button>(relaxed = true)
                BindingManager.add(variable)
                BindingManager.add(button)
                BindingManager.reset()
                BindingManager.update()
                verify(exactly = 0) { variable.update() }
                verify(exactly = 0) { button.update(any()) }
            }
        }
    }
}