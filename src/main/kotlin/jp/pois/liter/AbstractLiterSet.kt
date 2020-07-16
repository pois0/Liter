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

abstract class AbstractLiterSet<out E, out T> internal constructor() {
    abstract var hasNext: Boolean
        protected set

    abstract fun read(): T?

    protected abstract fun readE(): E?

    protected abstract fun getSavedElementsIterator(): Iterator<E>

    protected inner class LiterIterator : Iterator<E> {
        private val savedElements = getSavedElementsIterator()
        private var nextValue: E? = null

        override fun hasNext(): Boolean {
            if (nextValue != null) return true
            if (savedElements.hasNext()) return true
            if (!hasNext) return false

            val next = readE()
            return if (next != null) {
                nextValue = next
                true
            } else false
        }

        override fun next(): E {
            return if (savedElements.hasNext()) {
                savedElements.next()
            } else {
                nextValue?.also {
                    nextValue = null
                }
                    ?: readE()
                    ?: throw NoSuchElementException()
            }
        }
    }
}
