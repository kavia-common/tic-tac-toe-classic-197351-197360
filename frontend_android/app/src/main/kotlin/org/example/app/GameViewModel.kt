package org.example.app

/**
 * A lightweight "ViewModel" without AndroidX lifecycle dependencies (to keep this container self-contained
 * and avoid introducing additional Gradle DCL dependencies).
 *
 * The Activity subscribes to state changes via [onStateChanged] callback.
 */
class GameViewModel(
    private val engine: GameEngine = GameEngine()
) {
    data class Scores(
        val xWins: Int = 0,
        val oWins: Int = 0,
        val draws: Int = 0
    )

    data class UiState(
        val state: GameEngine.State = GameEngine().newGame(),
        val scores: Scores = Scores()
    )

    var onStateChanged: ((UiState) -> Unit)? = null
        set(value) {
            field = value
            value?.invoke(uiState)
        }

    private var uiState: UiState = UiState(state = engine.newGame())

    fun getState(): UiState = uiState

    fun onCellTapped(index: Int) {
        val current = uiState
        val nextState = engine.play(current.state, index)
        if (nextState === current.state) {
            // No-op move (invalid), keep state.
            return
        }
        setState(current.copy(state = nextState))
    }

    /**
     * Restarts the game. If [countScoreFromPreviousRound] is true, we update scores based on the current game's outcome.
     */
    fun restart(countScoreFromPreviousRound: Boolean = true) {
        val current = uiState
        val updatedScores = if (countScoreFromPreviousRound) {
            when (val outcome = current.state.outcome) {
                is GameEngine.Outcome.Win -> {
                    if (outcome.winner == GameEngine.Player.X) {
                        current.scores.copy(xWins = current.scores.xWins + 1)
                    } else {
                        current.scores.copy(oWins = current.scores.oWins + 1)
                    }
                }
                GameEngine.Outcome.Draw -> current.scores.copy(draws = current.scores.draws + 1)
                GameEngine.Outcome.InProgress -> current.scores
            }
        } else {
            current.scores
        }

        // For a nice feel, let the winner start next game, otherwise keep alternating starting player.
        val nextStartingPlayer = when (val outcome = current.state.outcome) {
            is GameEngine.Outcome.Win -> outcome.winner
            else -> current.state.currentPlayer
        }

        setState(
            UiState(
                state = engine.newGame(startingPlayer = nextStartingPlayer),
                scores = updatedScores
            )
        )
    }

    fun resetScoresAndRestart() {
        setState(
            UiState(
                state = engine.newGame(),
                scores = Scores()
            )
        )
    }

    private fun setState(newState: UiState) {
        uiState = newState
        onStateChanged?.invoke(uiState)
    }
}
