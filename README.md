# Liter

[![Kotlin](https://img.shields.io/badge/Kotlin-1.3.72-orange.svg)](https://kotlinlang.org)
[ ![Download](https://api.bintray.com/packages/pois/KotlinLibs/Liter/images/download.svg) ](https://bintray.com/pois/KotlinLibs/Liter/_latestVersion)
[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)

Liter provides List and Map storing elements generated by the iterator.

It depends on the standard library only.

## Usage

```kotlin
import jp.pois.liter.LiterList
import jp.pois.liter.LiterMap
import jp.pois.liter.literList
import jp.pois.liter.literMap

fun main() {
    val listIterator = iterator {
        for (i in 0 until 100) {
            yield(i)
            println("$i is now generated!")
        }
    }

    val literList: LiterList<Int> = listIterator.literList() // Wraps the iterator

    val fifth = literList[5] // LiterList extends List, so you can use get operator
    /*
    Output:
        0 is now generated!
        1 is now generated!
        2 is now generated!
        3 is now generated!
        4 is now generated!
        5 is now generated!
     */
    println(fifth) // 5
    
    println(literList[1]) // 1

    val tenth = literList[10]
    /*
    Output:
        6 is now generated!
        7 is now generated!
        8 is now generated!
        9 is now generated!
        10 is now generated!
     */
    println(tenth) // 10

    val mapIterator = iterator {
        ('a' .. 'z').withIndex().forEach { (i, c) ->
            yield(c to i)
            println("${c to i} is now generated!")
        }
    }

    val literMap: LiterMap<Char, Int> = mapIterator.literMap() // Wraps the iterator

    val c = literMap['c'] // LiterList extends Map, so you can use get operator
    /*
    Output:
        (a, 0) is now generated!
        (b, 1) is now generated!
        (c, 2) is now generated!
     */

    println(c) // 2
    println(literMap['a']) // 0

    val f = literMap['f']
    /*
    Output:
        (d, 3) is now generated!
        (e, 4) is now generated!
        (f, 5) is now generated!
     */

    println(f) // 5
}

```
