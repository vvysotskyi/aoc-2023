import java.util.concurrent.atomic.AtomicInteger
import kotlin.math.pow

fun main() {
    fun part1(input: List<String>): Int {
        return input.sumOf { line ->
            val winningNumbers = line.substring(line.indexOf(":") + 1, line.indexOf("|"))
                .trim().splitToSequence(Regex("\\s+"))
                .toSet()

            val numbersCount = line.substring(line.indexOf("|") + 1)
                .trim().splitToSequence(Regex("\\s+"))
                .filter { it in winningNumbers }
                .count()

            2.toDouble().pow(numbersCount - 1).toInt()
        }
    }

    fun part2(input: List<String>): Int {
        val cardsCount = mutableMapOf<Int, AtomicInteger>()
        input.forEach { line ->
            val gameInfo = line.split(":")
            val cardNumber = gameInfo[0].removePrefix("Card ").trim().toInt()

            val cardCopies = cardsCount.computeIfAbsent(cardNumber) { _ -> AtomicInteger() }.incrementAndGet()

            val winningNumbers = line.substring(line.indexOf(":") + 1, line.indexOf("|"))
                .trim().splitToSequence(Regex("\\s+"))
                .map { it.toInt() }
                .toSet()

            line.substring(line.indexOf("|") + 1)
                .trim().splitToSequence(Regex("\\s+"))
                .map { it.toInt() }
                .filter { winningNumbers.contains(it) }
                .forEachIndexed { index, _ ->
                    cardsCount.computeIfAbsent(cardNumber + index + 1) { _ -> AtomicInteger() }.addAndGet(cardCopies)
                }
        }
        return cardsCount.values.sumOf { it.get() }
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day04_test")
    check(part1(testInput) == 13)
    check(part2(testInput) == 30)

    val input = readInput("Day04")
    part1(input).println()
    part2(input).println()
}
