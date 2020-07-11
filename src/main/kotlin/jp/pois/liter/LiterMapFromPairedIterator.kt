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

class LiterMapFromPairedIterator<K, V>(private val origin: Iterator<Pair<K, V>>, map: MutableMap<K, V>) :
    LiterMap<K, V>(map) {
    override var hasNext: Boolean = origin.hasNext()

    constructor(origin: Iterator<Pair<K, V>>) : this(origin, HashMap())

    override fun read(): Pair<K, V> {
        if (!hasNext) throw NoSuchElementException()

        val next = origin.next()
        map.putIfAbsent(next.first, next.second)
        hasNext = origin.hasNext()

        return next
    }

    override fun readAll() {
        if (!hasNext) return

        origin.forEach { (key, value) ->
            map.putIfAbsent(key, value)
        }

        hasNext = false
    }

    override fun readValue(key: K): V? {
        if (!hasNext) return null

        while (origin.hasNext()) {
            val (k, v) = origin.next()
            map.putIfAbsent(k, v)
            if (k == key) {
                hasNext = origin.hasNext()
                return v
            }
        }

        hasNext = false
        return null
    }

    override fun readKey(value: V): K? {
        if (!hasNext) return null

        while (origin.hasNext()) {
            val (k, v) = origin.next()
            map.putIfAbsent(k, v)
            if (v == value) {
                hasNext = origin.hasNext()
                return k
            }
        }

        hasNext = false
        return null
    }
}

fun <K, V> Iterator<Pair<K, V>>.literMap(): LiterMapFromPairedIterator<K, V> = LiterMapFromPairedIterator(this)

fun <K, V> Iterator<Pair<K, V>>.literMap(map: MutableMap<K, V>) = LiterMapFromPairedIterator(this, map)

fun <K, V> Iterable<Pair<K, V>>.literMap(): LiterMapFromPairedIterator<K, V> = LiterMapFromPairedIterator(iterator())

fun <K, V> Iterable<Pair<K, V>>.literMap(map: MutableMap<K, V>): LiterMapFromPairedIterator<K, V> =
    LiterMapFromPairedIterator(iterator(), map)

