import kotlin.math.max
import kotlin.math.min

fun main() {
    fun part1(input: List<String>): Int {
        return EngineSchematicPartNumberProcessor(input).evaluate()
    }

    fun part2(input: List<String>): Int {
        return EngineSchematicGearRatioProcessor(input).evaluate()
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day03_test")
    check(part1(testInput) == 4361)
    check(part2(testInput) == 467835)

    val input = readInput("Day03")
    part1(input).println()
    part2(input).println()
}

fun Char.isSymbol(): Boolean {
    return !this.isDigit() && this != '.'
}

fun Char.isGear(): Boolean {
    return this == '*'
}

abstract class EngineSchematicProcessor(protected val input: List<String>) {

    abstract fun evaluate(): Int

    protected abstract fun consumeNumber(rowIndex: Int, numberStart: Int, numberEnd: Int)

    protected fun processSchematic() {
        for (rowIndex in input.indices) {
            val row = input[rowIndex]
            var numberStart = -1
            var numberEnd = -1
            for (columnIndex in row.indices) {
                val char = row[columnIndex]
                if (char.isDigit()) {
                    if (numberStart < 0) {
                        numberStart = columnIndex
                    }
                } else if (numberStart >= 0) {
                    numberEnd = columnIndex
                }

                if (hasCompleteNumber(row, columnIndex, numberStart, numberEnd)) {
                    if (columnIndex == row.length - 1 && char.isDigit()) {
                        // last char in row was digit
                        numberEnd = columnIndex + 1
                    }
                    consumeNumber(rowIndex, numberStart, numberEnd)

                    numberStart = -1
                    numberEnd = -1
                }
            }
        }
    }

    protected fun neighbours(row: String, numberStart: Int, numberEnd: Int) = max(numberStart - 1, 0)..min(numberEnd, row.length - 1)

    private fun hasCompleteNumber(row: String, position: Int, numberStart: Int, numberEnd: Int) =
        numberStart >= 0 && (numberEnd > 0 || position == row.length - 1)
}

class EngineSchematicPartNumberProcessor(input: List<String>) : EngineSchematicProcessor(input) {
    private var sum = 0

    override fun evaluate(): Int {
        processSchematic()
        return sum
    }

    override fun consumeNumber(rowIndex: Int, numberStart: Int, numberEnd: Int) {
        if (isAdjacent(rowIndex, numberStart, numberEnd)) {
            sum += input[rowIndex].substring(numberStart, numberEnd).toInt()
        }
    }

    private fun hasSymbolAbove(rowIndex: Int, numberStart: Int, numberEnd: Int) =
        rowIndex > 0 && neighbours(input[rowIndex - 1], numberStart, numberEnd).any { input[rowIndex - 1][it].isSymbol() }

    private fun hasSymbolBelow(rowIndex: Int, numberStart: Int, numberEnd: Int) =
        rowIndex < input.size - 1 && neighbours(input[rowIndex + 1], numberStart, numberEnd).any { input[rowIndex + 1][it].isSymbol() }

    private fun hasSymbolLeft(rowIndex: Int, numberStart: Int) =
        numberStart > 0 && input[rowIndex][numberStart - 1].isSymbol()

    private fun hasSymbolRight(rowIndex: Int, numberEnd: Int) =
        numberEnd <= input[rowIndex].length - 1 && input[rowIndex][numberEnd].isSymbol()

    private fun isAdjacent(rowIndex: Int, numberStart: Int, numberEnd: Int) =
        hasSymbolAbove(rowIndex, numberStart, numberEnd)
                || hasSymbolBelow(rowIndex, numberStart, numberEnd)
                || hasSymbolLeft(rowIndex, numberStart)
                || hasSymbolRight(rowIndex, numberEnd)
}

class EngineSchematicGearRatioProcessor(input: List<String>) : EngineSchematicProcessor(input) {
    private val gears = mutableMapOf<Pair<Int, Int>, MutableList<Int>>()

    override fun evaluate(): Int {
        processSchematic()
        return gears.values.asSequence()
            .filter { it.size == 2 }
            .map { it[0] * it[1] }
            .reduce { a, b -> a + b }
    }

    override fun consumeNumber(rowIndex: Int, numberStart: Int, numberEnd: Int) {
        getNeighbourGears(rowIndex, numberStart, numberEnd).forEach {
            gears.computeIfAbsent(it) { _ -> mutableListOf() } += input[rowIndex].substring(numberStart, numberEnd).toInt()
        }
    }

    private fun upperGears(rowIndex: Int, numberStart: Int, numberEnd: Int) =
        if (rowIndex > 0) {
            neighbours(input[rowIndex - 1], numberStart, numberEnd)
                .filter { input[rowIndex - 1][it].isGear() }
                .map { rowIndex - 1 to it }
        } else {
            emptyList()
        }

    private fun lowerGears(rowIndex: Int, numberStart: Int, numberEnd: Int) =
        if (rowIndex < input.size - 1) {
            neighbours(input[rowIndex + 1], numberStart, numberEnd)
                .filter { input[rowIndex + 1][it].isGear() }
                .map { rowIndex + 1 to it }
        } else {
            emptyList()
        }

    private fun leftGears(row: String, rowIndex: Int, numberStart: Int) =
        if (numberStart > 0 && row[numberStart - 1].isGear()) {
            listOf(rowIndex to numberStart - 1)
        } else {
            emptyList()
        }

    private fun rightGears(row: String, rowIndex: Int, numberEnd: Int) =
        if (numberEnd <= row.length - 1 && row[numberEnd].isGear()) {
            listOf(rowIndex to numberEnd)
        } else {
            emptyList()
        }

    private fun getNeighbourGears(rowIndex: Int, numberStart: Int, numberEnd: Int): List<Pair<Int, Int>> {
        val neighbourGears = mutableListOf<Pair<Int, Int>>()
        neighbourGears += upperGears(rowIndex, numberStart, numberEnd)
        neighbourGears += lowerGears(rowIndex, numberStart, numberEnd)
        neighbourGears += leftGears(input[rowIndex], rowIndex, numberStart)
        neighbourGears += rightGears(input[rowIndex], rowIndex, numberEnd)

        return neighbourGears
    }
}
