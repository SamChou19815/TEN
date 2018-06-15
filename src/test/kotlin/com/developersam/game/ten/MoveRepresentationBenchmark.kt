package com.developersam.game.ten

import org.junit.Test

/**
 * [MoveRepresentationBenchmark] tests the performance of different representation of move.
 */
class MoveRepresentationBenchmark {

    /**
     * [repeat] represents number of repeats.
     */
    private val repeats = 1 shl 15

    /**
     * [PairMove] represents a move at [a], [b].
     */
    private data class PairMove(val a: Int, val b: Int)

    /**
     * [testPairMove] tests the speed of a pair based move.
     */
    @Test
    fun testPairMove() {
        val move = PairMove(a = 4, b = 4)
        var acc = 0
        repeat(times = repeats) {
            repeat(times = repeats) {
                acc += move.a
                acc += move.b
            }
        }
        println(acc)
    }

    /**
     * [testArrayMove] tests the speed of an array based move.
     */
    @Test
    fun testArrayMove() {
        val move = intArrayOf(4, 4)
        var acc = 0
        repeat(times = repeats) {
            repeat(times = repeats) {
                acc += move[0]
                acc += move[1]
            }
        }
        println(acc)
    }

    /**
     * [testIntMove] tests the speed of an int based move.
     */
    @Test
    fun testIntMove() {
        val move = 4 * 9 * 4
        var acc = 0
        repeat(times = repeats) {
            repeat(times = repeats) {
                acc += move / 9
                acc += move % 9
            }
        }
        println(acc)
    }

}
