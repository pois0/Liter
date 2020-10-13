/*
 * Copyright 2020 poispois
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

@file:Suppress("MemberVisibilityCanBePrivate")

package jp.pois.liter

import kotlin.experimental.ExperimentalTypeInference

class LiterSet<T>(internal val origin: Iterator<T>) : AbstractLiterSet<T, T>(), Set<T> {
    private val set = LinkedHashSet<T>()

    val savedElements: Set<T> = set

    override var hasNext: Boolean = origin.hasNext()


    override val size: Int
        get() {
            if (hasNext) {
                readAll()
            }

            return set.size
        }

    override fun read(): T? {
        if (!hasNext) throw NoSuchElementException()

        origin.forEach {
            if (set.add(it)) {
                hasNext = origin.hasNext()
                return it
            }
        }

        hasNext = false
        return null
    }

    fun readAll() {
        if (!hasNext) return

        origin.forEach {
            set.add(it)
        }

        hasNext = false
    }

    fun readUntil(element: T): Boolean {
        if (!hasNext) throw NoSuchElementException()

        origin.forEach {
            if (it == element) {
                hasNext = origin.hasNext()
                return true
            }
        }

        return false
    }

    override fun contains(element: T): Boolean = set.contains(element) || readUntil(element)

    override fun containsAll(elements: Collection<T>): Boolean = elements.all { contains(it) }

    override fun getSavedElementsIterator(): Iterator<T> = set.iterator()

    override fun isEmpty(): Boolean = !hasNext && set.isEmpty()

    override fun iterator(): Iterator<T> = LiterIterator()

    override fun readE(): T? = read()
}

fun <T> Iterator<T>.literSet(): LiterSet<T> = LiterSet(this)

fun <T> Iterable<T>.literSet(): LiterSet<T> = LiterSet(iterator())

fun <T> Sequence<T>.literSet(): LiterSet<T> = LiterSet(iterator())

fun <T> LiterSet<T>.toSet(): Set<T> {
    if (hasNext) {
        readAll()
    }

    if (isEmpty()) return emptySet()

    return savedElements
}

@OptIn(ExperimentalTypeInference::class)
fun <T> buildLiterSet(
    @BuilderInference block: suspend SequenceScope<T>.() -> Unit
): LiterSet<T> = LiterSet(iterator(block))
