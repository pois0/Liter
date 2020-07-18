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

class LiterList<T>(internal val origin: Iterator<T>, private val list: MutableList<T>) : List<T> {
    val savedElements: List<T> = list

    internal var hasNext: Boolean = origin.hasNext()
        private set

    override val size: Int
        get() {
            if (hasNext) {
                readAll()
            }

            return list.size
        }

    constructor(origin: Iterator<T>) : this(origin, ArrayList<T>())

    private fun checkNext(): Boolean {
        val hasNext = origin.hasNext()
        this.hasNext = hasNext
        return hasNext
    }

    fun read(): T {
        if (!hasNext) throw NoSuchElementException()

        val next = origin.next()
        list.add(next)
        hasNext = origin.hasNext()

        return next
    }

    fun read(n: Int): T {
        require(n > 0) { "Argument n must be positive" }

        val array = Array<Any?>(n) {
            if (!origin.hasNext()) throw NoSuchElementException()
            origin.next()
        }
        hasNext = origin.hasNext()

        @Suppress("UNCHECKED_CAST")
        list.addAll(array as Array<T>)

        return array[n - 1]
    }

    fun readAll() {
        if (!hasNext) return

        origin.forEach {
            list.add(it)
        }

        hasNext = false
    }

    override fun contains(element: T): Boolean = indexOf(element) >= 0

    override fun containsAll(elements: Collection<T>): Boolean = elements.all { contains(it) }

    override fun get(index: Int): T {
        if (index < list.size) return list[index]
        if (!hasNext) throw NoSuchElementException()

        return read(index - list.size + 1)
    }

    override fun indexOf(element: T): Int {
        val index = list.indexOf(element)
        if (index > 0) return index
        if (!hasNext) return -1

        while (origin.hasNext()) {
            val value = read()
            if (value == element) {
                hasNext = origin.hasNext()
                return list.lastIndex
            }
        }

        hasNext = false
        return -1
    }

    override fun isEmpty(): Boolean = hasNext || list.isEmpty()

    override fun iterator(): Iterator<T> = if (hasNext) LiterListIterator() else list.iterator()

    override fun lastIndexOf(element: T): Int {
        if (!hasNext) return list.lastIndexOf(element)

        var current = list.size
        var last = list.lastIndexOf(element)
        while (origin.hasNext()) {
            val next = origin.next()
            if (next == element) last = current
            current++
        }

        hasNext = false
        return last
    }

    override fun listIterator(): ListIterator<T> = if (hasNext) LiterListListIterator() else list.listIterator()

    override fun listIterator(index: Int): ListIterator<T> =
        if (hasNext) LiterListListIterator(index) else list.listIterator(index)

    override fun subList(fromIndex: Int, toIndex: Int): List<T> {
        if (hasNext) {
            require(fromIndex in 0 until toIndex)

            val mustRead = toIndex - list.lastIndex
            if (mustRead > 0) {
                read(toIndex - list.lastIndex)
            }
        }

        return list.subList(fromIndex, toIndex)
    }

    private inner class LiterListIterator : Iterator<T> {
        private var index = 0

        override fun hasNext(): Boolean = index < list.size || origin.hasNext()

        override fun next(): T {
            if (index < list.size) return list[index++]

            index++
            return this@LiterList.read()
        }

    }

    private inner class LiterListListIterator(private var index: Int = 0) : ListIterator<T> {
        init {
            if (index > list.size) {
                read(index - list.size)
            }
        }

        override fun hasNext(): Boolean = index < list.size || origin.hasNext()

        override fun hasPrevious(): Boolean = index > 0

        override fun next(): T {
            if (index < list.size) return list[index++]

            index++
            return this@LiterList.read()
        }

        override fun nextIndex(): Int = index

        override fun previous(): T = list[--index]

        override fun previousIndex(): Int = index - 1
    }
}

fun <T> Iterator<T>.literList(): LiterList<T> = LiterList(this)

fun <T> Iterator<T>.literList(list: MutableList<T>) = LiterList(this, list)

fun <T> Iterable<T>.literList(): LiterList<T> = LiterList(iterator())

fun <T> Iterable<T>.literList(list: MutableList<T>) = LiterList(iterator(), list)

fun <T> Sequence<T>.literList(): LiterList<T> = LiterList(iterator())

fun <T> Sequence<T>.literList(list: MutableList<T>) = LiterList(iterator(), list)

fun <T> LiterList<T>.toList(): List<T> {
    readAll()
    return savedElements
}


