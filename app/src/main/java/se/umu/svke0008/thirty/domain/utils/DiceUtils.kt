package se.umu.svke0008.thirty.domain.utils


import se.umu.svke0008.thirty.domain.model.Dice
import kotlin.random.Random

/**
 * A utility object for handling dice-related operations, including dice creation, random number generation,
 * dice combination analysis and more.
 */
object DiceUtils {

    /**
     * Generates a random dice number between 1 and 6 (inclusive).
     *
     * @return A random dice number.
     */
    fun getRandomDiceNumber(): Int {
        return Random.nextInt(1, 7)
    }


    /**
     * Creates a list of new [Dice] instances based on the given amount.
     * Each dice has a random number assigned to it.
     *
     * @param amount The number of dice to create.
     * @return A list of new [Dice] instances.
     */
    fun createDice(amount: Int): List<Dice> {
        val newDice = mutableListOf<Dice>()
        for (i in 0 until amount) {
            newDice += Dice(
                id = newDice.size,
                amount = getRandomDiceNumber(),
                locked = false,
                selected = false
            )
        }
        return newDice
    }

    /**
     * Checks if a given list of dice forms a valid combination with respect to a target value.
     *
     * The function validates a combination in the following order:
     * 1. Returns false if the list dice is empty
     * 2. If the target value is less than or equal to 3, it checks whether all dice in the
     *    combination have an amount less than or equal to 3.
     *    If yes, it returns true, indicating that it's a valid combination.
     * 3. If the target value is greater than 3, it calls the [isValidCombination] function with
     *    the list of dice and the target value.
     *
     *
     * @param combinations The list of dice to validate.
     * @param target The target sum value for each combination of dice.
     * @return A boolean value indicating whether the dice combination is valid (true) or not (false).
     */
    private fun checkIfCombinationIsValid(combinations: List<Dice>, target: Int): Boolean {
        return when {
            combinations.isEmpty() -> return false
            target <= 3 -> combinations.all { it.amount <= 3 }
            else -> isValidCombination(combinations, target)
        }

    }

    /**
     * Calculates and returns the sum of amounts of all dice in a given combination, if it is valid.
     *
     * @param combinations A list of Dice objects representing a combination.
     * @param target The target value that the sum of the dice amounts in a valid combination should reach.
     * @return The sum of dice amounts in the combination if it is valid, otherwise 0.
     */
    fun calculatedValidCombinations(combinations: List<Dice>, target: Int): Int {
        return when {
            checkIfCombinationIsValid(combinations, target) -> combinations.sumOf { it.amount }
            else -> 0
        }
    }


    /**
     * Checks if a given selection of dice can be grouped into valid combinations,
     * where the sum of the dice amount in each combination equals a target value.
     * If the list of valid combinations is empty, or if there are any remaining
     * dice in the selection after checking all combinations, the function will
     * return false indicating the dice selection is invalid.
     *
     * Note: Each dice can only be used once.
     *
     * @param diceSelection The list of dice to be grouped into combinations.
     * @param target The target sum value for each combination of dice.
     * @return A boolean indicating whether the dice selection is valid (true) or not (false).
     */
    private fun isValidCombination(diceSelection: List<Dice>, target: Int): Boolean {
        val validCombinations = mergeUniqueCombinations(diceSelection, target)
        if (validCombinations.isEmpty()) {
            return false
        }

        val combinationsDiceAmounts = validCombinations.map { it.map { dice -> dice.amount }}
        val selectionDiceAmounts = diceSelection.map { it.amount }.toMutableList()

        for (combinationDiceAmounts in combinationsDiceAmounts) {
            for (die in combinationDiceAmounts) {
                selectionDiceAmounts.remove(die)
            }

            if (selectionDiceAmounts.isNotEmpty()) {
                return false
            }
        }

        return true
    }




    /**
     * Returns a list of unique combinations of dice, based on their IDs.
     * The combinations are rotated to provide unique sets of combinations
     * where each dice is used only once across all combinations in a set.
     *
     * @param allCombinations The list of all possible combinations of dice.
     * @return A list of unique combinations sets.
     */
    private fun getUniqueCombinations(allCombinations: List<Combination>): List<List<Combination>> {
        val uniqueCombinationSets = mutableListOf<List<Combination>>()

        for (i in allCombinations.indices) {
            val rotatedCombinations = allCombinations.drop(i) + allCombinations.take(i)

            val uniqueSet = mutableListOf<Combination>()
            val usedDiceIds = mutableSetOf<Int>()

            for (combination in rotatedCombinations) {
                val diceIdsInCombination = combination.map { it.id }.toSet()

                if (diceIdsInCombination.intersect(usedDiceIds).isEmpty()) {
                    uniqueSet.add(combination)
                    usedDiceIds.addAll(diceIdsInCombination)
                }
            }

            if (uniqueSet.isNotEmpty()) {
                uniqueCombinationSets.add(uniqueSet)
            }
        }

        return uniqueCombinationSets
    }
    /**
     * Checks if two lists of dice contain the same dice, based on their amounts.
     * The lists are first sorted by the dice amount to ensure correct comparison.
     *
     * @param list1 The first list of dice.
     * @param list2 The second list of dice.
     * @return A boolean indicating whether the lists contain the same dice (true) or not (false).
     */
    private fun listsContainSameDice(list1: List<Dice>, list2: List<Dice>): Boolean {
        if (list1.size != list2.size) {
            return false
        }
        val sortedList1 = list1.sortedBy { it.amount }
        val sortedList2 = list2.sortedBy { it.amount }

        for (i in sortedList1.indices) {
            if (sortedList1[i].amount != sortedList2[i].amount) {
                return false
            }
        }
        return true
    }

    /**
     * Merges unique combinations of dice, where each dice is used only once across all combinations.
     * The function first finds all possible combinations, then filters out duplicates,
     * and finally merges the unique sets into a list of combinations.
     *
     * @param diceSelection The list of dice to be grouped into combinations.
     * @param target The target sum value for each combination of dice.
     * @return A list of combinations with unique dice.
     */
    private fun mergeUniqueCombinations(diceSelection: List<Dice>, target: Int): List<Combination> {
        val allCombinations = findCombinations(diceSelection, target).sortByDiceAmount()
        val uniqueCombinations = getUniqueCombinations(allCombinations)
        val mergedList = mutableListOf<List<Dice>>()

        for (uniqueSet in uniqueCombinations) {
            val flatSet = uniqueSet.flatten()
            if (mergedList.none { listsContainSameDice(it, flatSet) }) {
                mergedList.add(flatSet)
            }
        }

        return mergedList
    }


    /**
     * Identifies low combinations, where each die has a number less than or equal to 3.
     *
     * @param dices The list of dice to check.
     * @return A list of low combinations.
     */
    private fun lowCombinations(dices: List<Dice>): List<Combination> {
        val combinationList = mutableListOf<Combination>()
        for (dice in dices) {
            if (dice.amount <= 3) {
                combinationList.add(mutableListOf(dice))
            }
        }
        return combinationList
    }

    /**
     * Recursively finds all possible combinations of [Dice] in a given list that sum up to a target value.
     * This method implements a classic problem-solving technique known as 'backtracking'.
     *
     * The function splits the problem into two sub-problems:
     * 1. Find combinations that include the first dice in the list 'combinationsWithFirstDice'
     * 2. Find combinations that exclude the first dice in the list 'combinationsWithoutFirstDice'
     *
     * In the end, the results of these two sub-problems are combined to give the final result.
     *
     *
     * @param diceList The list of [Dice] instances to search through.
     * @param target The target sum value that the dice amounts in a combination should add up to.
     * @return A list of valid [Combination] instances found. If no valid combinations are found, an empty list is returned.
     */
    private fun findCombinations(diceList: List<Dice>, target: Int): List<Combination> {
        if (target == 0) return listOf(mutableListOf())
        if (diceList.isEmpty() || target < 0) return emptyList()

        val firstDice = diceList.first()
        val restDices = diceList.drop(1)

        val combinationsWithoutFirstDice = findCombinations(restDices, target)
        val combinationsWithFirstDice =
            findCombinations(restDices, target - firstDice.amount).map { it + firstDice }

        return combinationsWithoutFirstDice + combinationsWithFirstDice
    }


    /**
     * Finds the maximum score that can be achieved with valid combinations of dice.
     *
     * @param target The target sum value.
     * @param dices The list of dice.
     * @return The maximum achievable score.
     */
    fun findMaxScoreOfCombinations(target: Int, dices: List<Dice>): Int {
        var score = 0
        val validCombinations = findLargestAmountOfValidCombinations(target, dices)

        for (combination in validCombinations) {
            score += combination.scoreFromCombination()
        }

        return score
    }

    /**
     * Finds the largest number of valid dice combinations that meet a given target value.
     *
     * For targets less than or equal to 3, the function returns only the low combinations.
     * For targets greater than 3, the function generates all possible combinations and then
     * filters them to ensure no dice is reused across multiple combinations.
     *
     * The function sorts the combinations by their size, starting with the smallest.
     * This approach ensures the minimal usage of dice, thus maximizing the possibility of a die
     * being available for other combinations.
     *
     * Note: Although this approach is feasible, it might not always produce the optimal result.
     * A comprehensive algorithm that considers all possible sequence orders would be needed to
     * ensure finding the absolute maximum amount of combinations.
     *
     * Each dice within a combination is compared against every other dice in the already validated
     * combinations. If a dice with the same ID is found in any of the validated combinations,
     * the current combination is deemed invalid, and the algorithm proceeds to the next combination.
     *
     * @param target The target value against which the dice combinations are validated.
     * @param diceList The list of dice from which the combinations are generated.
     * @return A list of valid dice combinations that meet the specified target value.
     */
    fun findLargestAmountOfValidCombinations(target: Int, diceList: List<Dice>): List<Combination> {
        if (target <= 3) {
            return lowCombinations(diceList)
        }
        val combinations = findCombinations(diceList, target)

        val validCombinations = mutableListOf<Combination>()

        for (combination in combinations.sortedBy { it.size }) {
            var combinationOk = true

            for (dice in combination) {
                for (validCombination in validCombinations) {
                    for (validDice in validCombination) {
                        if (dice.id == validDice.id) {
                            combinationOk = false
                            break
                        }
                    }
                    if (!combinationOk) break
                }
                if (!combinationOk) break
            }

            if (combinationOk) {
                validCombinations.add(combination)
            }
        }

        return validCombinations
    }


    /**
     *  Note: Experimental
     */
    private fun findMaxScoreOfCombinations(
        combinations: List<Combination>,
        usedDices: Set<Int> = emptySet(),
        startIndex: Int = 0
    ): Int {
        if (startIndex >= combinations.size) return 0

        val skipCombinationScore =
            findMaxScoreOfCombinations(combinations, usedDices, startIndex + 1)

        val currentCombination = combinations[startIndex]
        val currentCombinationDices = currentCombination.map { it.id }.toSet()

        return if (usedDices.intersect(currentCombinationDices).isNotEmpty()) {
            // This combination uses a dice that's already been used, so we skip it
            skipCombinationScore
        } else {
            // This combination is valid, so we consider both possibilities: using it or skipping it
            val useCombinationScore =
                currentCombination.scoreFromCombination() +
                        findMaxScoreOfCombinations(
                            combinations,
                            (usedDices + currentCombinationDices),
                            startIndex + 1
                        )
            maxOf(skipCombinationScore, useCombinationScore)
        }
    }

}