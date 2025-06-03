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
import io.mockk.mockkObject
import io.mockk.unmockkObject
import io.mockk.verify

class BindingsTest : DescribeSpec() {
    init {
        beforeTest { mockkObject(BindingManager) }
        afterTest { unmockkObject(BindingManager) }

        describe("variable") {
            it("registers with BindingManager") {
                val variable = variable { "hello" }
                verify(exactly = 1) { BindingManager.add(variable) }
            }
        }

        describe("range") {
            it("registers with BindingManager") {
                val range = range { 1.0 }
                verify(exactly = 1) { BindingManager.add(range) }
            }
        }

        describe("button") {
            it("registers with BindingManager") {
                val button = button { true }
                verify(exactly = 1) { BindingManager.add(button) }
            }
        }
    }
}