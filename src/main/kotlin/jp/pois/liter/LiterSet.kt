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

package jp.pois.liter

class LiterSet<T>(private val origin: Iterator<T>) : Set<T> {
    private val set = LinkedHashSet<T>()

    val savedElements: Set<T> = set

    var hasNext: Boolean = origin.hasNext()
        private set

    override val size: Int
        get() {
            if (hasNext) {
                readAll()
            }

            return set.size
        }

    fun read(): T? {
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

    override fun isEmpty(): Boolean = set.isEmpty() && !hasNext

    override fun iterator(): Iterator<T> = LiterSetIterator()

    internal inner class LiterSetIterator : Iterator<T> {
        private val setIterator = set.iterator()
        private var nextValue: T? = null

        override fun hasNext(): Boolean {
            if (setIterator.hasNext()) return true
            if (!hasNext) return false

            val next = read()
            return if (next != null) {
                nextValue = next
                true
            } else false
        }

        override fun next(): T {
            return if (setIterator.hasNext()) {
                setIterator.next()
            } else {
                nextValue?.also {
                    nextValue = null
                }
                    ?: read()
                    ?: throw NoSuchElementException()
            }
        }
    }
}
