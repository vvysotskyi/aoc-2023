import java.lang.Integer.max

fun main() {
    val maxValues = mapOf(
        "red" to 12,
        "green" to 13,
        "blue" to 14
    )

    fun gameSubsets(line: String): Pair<Int, Sequence<String>> {
        val gameInfo = line.split(":")
        val gameId = gameInfo[0].removePrefix("Game ").toInt()
        val subsets = gameInfo[1].splitToSequence(";")
        return Pair(gameId, subsets)
    }

    fun part1(input: List<String>) = input.sumOf { line ->
        val (gameId, subsets) = gameSubsets(line)
        when {
            subsets
                .flatMap { it.splitToSequence(",") }
                .all {
                    val (count, colour) = it.trim().split(" ")
                    count.toInt() <= maxValues[colour]!!
                } -> gameId

            else -> 0
        }
    }

    fun part2(input: List<String>) = input.sumOf { line ->
        val (_, subsets) = gameSubsets(line)
        val maxCounts = mutableMapOf<String, Int>()
        subsets
            .flatMap { it.splitToSequence(",") }
            .forEach {
                val (count, colour) = it.trim().split(" ")
                maxCounts[colour] = max(maxCounts[colour] ?: 0, count.toInt())
            }

        maxCounts.values.reduce { a, b -> a * b }
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day02_test")
    check(part1(testInput) == 8)
    check(part2(testInput) == 2286)

    val input = readInput("Day02")
    part1(input).println()
    part2(input).println()
}
