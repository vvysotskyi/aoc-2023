fun main() {
    fun part1(input: List<String>): Int {
        return input.sumOf { line ->
            val first = line.toCharArray().find { it.isDigit() }!!.digitToInt()
            val last = line.toCharArray().findLast { it.isDigit() }!!.digitToInt()
            first * 10 + last
        }
    }

    fun part2(input: List<String>): Int {
        val wordToNum = mapOf(
            "one" to 1,
            "two" to 2,
            "three" to 3,
            "four" to 4,
            "five" to 5,
            "six" to 6,
            "seven" to 7,
            "eight" to 8,
            "nine" to 9
        )

        val allNums = wordToNum.keys.toMutableSet()
        (1..9).forEach { allNums += it.toString() }

        return input.sumOf { line ->
            val first = allNums.minBy { line.indexOfOrLen(it) }
            val last = allNums.maxBy { line.lastIndexOf(it) }
            val firstNum = wordToNum[first] ?: first.toInt()
            val lastNum = wordToNum[last] ?: last.toInt()

            firstNum * 10 + lastNum
        }
    }

    // test if implementation meets criteria from the description, like:
    var testInput = readInput("Day01_test1")
    check(part1(testInput) == 142)
    testInput = readInput("Day01_test2")
    check(part2(testInput) == 281)

    val input = readInput("Day01")
    part1(input).println()
    part2(input).println()
}

private fun CharSequence.indexOfOrLen(string: String): Int {
    val indexOf = indexOf(string)
    return if (indexOf >= 0) indexOf else length
}
