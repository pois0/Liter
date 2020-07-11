package jp.pois.liter

abstract class LiterMap<K, V> internal constructor(protected val map: MutableMap<K, V>) : Map<K, V> {
    val savedEntries: Map<K, V> = map

    abstract var hasNext: Boolean
        protected set

    override val entries: Set<Map.Entry<K, V>>
        get() {
            if (hasNext) {
                readAll()
            }

            return map.entries
        }

    override val keys: Set<K>
        get() {
            if (hasNext) {
                readAll()
            }

            return map.keys
        }

    override val size: Int
        get() {
            if (hasNext) {
                readAll()
            }

            return map.size
        }

    override val values: Collection<V>
        get() {
            if (hasNext) {
                readAll()
            }

            return map.values
        }

    abstract fun read(): Pair<K, V>

    abstract fun readAll()

    abstract fun readValue(key: K): V?

    abstract fun readKey(value: V): K?

    override fun containsKey(key: K): Boolean = map.containsKey(key) || readValue(key) != null

    override fun containsValue(value: V): Boolean = map.containsValue(value) || readKey(value) != null

    override fun get(key: K): V? = map[key] ?: readValue(key)

    override fun getOrDefault(key: K, defaultValue: V): V = map[key] ?: readValue(key) ?: defaultValue

    override fun isEmpty(): Boolean = hasNext || map.isEmpty()

    internal class PairEntry<out K, out V>(pair: Pair<K, V>) : Map.Entry<K, V> {
        override val key: K = pair.first
        override val value: V = pair.second
    }
}
