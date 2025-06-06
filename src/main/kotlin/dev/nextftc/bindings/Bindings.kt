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

@file:JvmName("Bindings")

package dev.nextftc.bindings

import java.util.function.Supplier

/**
 * Creates a [Variable] with the given [valueSupplier] and registers it with the [BindingManager].
 */
fun <T> variable(valueSupplier: Supplier<T>): Variable<T> = Variable(valueSupplier).also { BindingManager.add(it) }

/**
 * Creates a [Range] with the given [valueSupplier] and registers it with the [BindingManager].
 */
fun range(valueSupplier: Supplier<out Number>): Range = Range { valueSupplier.get().toDouble() }.also { BindingManager.add(it) }

/**
 * Creates a [Button] with the given [valueSupplier] and registers it with the [BindingManager].
 */
fun button(valueSupplier: Supplier<Boolean>): Button = Button(valueSupplier).also { BindingManager.add(it) }
