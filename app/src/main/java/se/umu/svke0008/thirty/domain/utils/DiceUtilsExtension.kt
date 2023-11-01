package se.umu.svke0008.thirty.domain.utils

import se.umu.svke0008.thirty.domain.model.Dice


/**
 * Represents a list of dice forming a combination.
 */
typealias Combination = List<Dice>

fun Combination.scoreFromCombination(): Int {
    return this.sumOf { dice -> dice.amount }
}

fun List<Combination>.sortByDiceIds(): List<Combination> {
    return this.map { combination -> combination.sortedBy { it.id } }
}

fun List<Combination>.sortByDiceAmount(): List<Combination> {
    return this.map { combination -> combination.sortedByDescending { it.amount } }
}

fun List<Combination>.printCombinations() {
    val string = if (this.isEmpty()) "-------- EmptyList -------"
    else "-------- Target: ${this.first().scoreFromCombination()} ---------------"
    println(string)
    this.forEach { combination ->
        println(combination.joinToString(separator = ", ") { "${it.id} = ${it.amount}" })
    }
    println("-------- Combination: ${this.size} ---------------")
    println()
}

