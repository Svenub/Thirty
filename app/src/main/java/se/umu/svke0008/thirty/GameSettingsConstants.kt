package se.umu.svke0008.thirty

object GameSettingsConstants {
    const val THROWS_AMOUNT = 3
    const val ROUNDS_AMOUNT = 10
    const val DICE_AMOUNT = 6
    const val WINNING_CONDITION = 30

    const val GAME_INSTRUCTIONS = """
    Welcome to Thirty, the dice game. Here's how you can play:

    1. At the start of each round, you are required to throw all the dice. 

    2. Once you've made the initial throw, you can choose to 'lock' any number of dice. To lock a die, simply click on it. After locking your chosen dice, you can throw the remaining ones again for a new outcome.

    3. When you're happy with your dice results, select a combination by locking the appropriate dice, and then end your turn. 

    4. Be careful, if the combination you select does not match the dice you've locked, your score for that turn will be zero.

    For assistance, you can enable the 'Help Assist' feature. When turned on, it provides suggestions for possible combinations based on your current dice roll. Enjoy your game!
"""


}

