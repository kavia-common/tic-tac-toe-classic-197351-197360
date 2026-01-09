package org.example.app

/**
 * Pure Tic Tac Toe rules/engine with no Android dependencies.
 *
 * Designed to be reusable for unit tests and for any future UI implementations.
 */
class GameEngine {
    enum class Player(val symbol: Char) {
        X('X'),
        O('O');

        fun other(): Player = if (this == X) O else X
    }

    sealed class Outcome {
        data object InProgress : Outcome()
        data class Win(val winner: Player, val line: IntArray) : Outcome()
        data object Draw : Outcome()
    }

    data class State(
        val board: CharArray = CharArray(9) { ' ' },
        val currentPlayer: Player = Player.X,
        val outcome: Outcome = Outcome.InProgress
    )

    /**
     * Attempts to play at [index] (0..8).
     * If move is invalid (occupied or game already over), returns the same state unchanged.
     */
    fun play(state: State, index: Int): State {
        if (index !in 0..8) return state
        if (state.outcome != Outcome.InProgress) return state
        if (state.board[index] != ' ') return state

        val newBoard = state.board.copyOf()
        newBoard[index] = state.currentPlayer.symbol

        val outcome = evaluateOutcome(newBoard)
        val nextPlayer = if (outcome == Outcome.InProgress) state.currentPlayer.other() else state.currentPlayer

        return state.copy(
            board = newBoard,
            currentPlayer = nextPlayer,
            outcome = outcome
        )
    }

    /** Returns a brand new game state (scores are handled outside in the ViewModel). */
    fun newGame(startingPlayer: Player = Player.X): State {
        return State(
            board = CharArray(9) { ' ' },
            currentPlayer = startingPlayer,
            outcome = Outcome.InProgress
        )
    }

    private fun evaluateOutcome(board: CharArray): Outcome {
        val wins = arrayOf(
            intArrayOf(0, 1, 2),
            intArrayOf(3, 4, 5),
            intArrayOf(6, 7, 8),
            intArrayOf(0, 3, 6),
            intArrayOf(1, 4, 7),
            intArrayOf(2, 5, 8),
            intArrayOf(0, 4, 8),
            intArrayOf(2, 4, 6)
        )

        for (line in wins) {
            val a = board[line[0]]
            val b = board[line[1]]
            val c = board[line[2]]
            if (a != ' ' && a == b && b == c) {
                val winner = if (a == GameEngine.Player.X.symbol) GameEngine.Player.X else GameEngine.Player.O
                return Outcome.Win(winner, line)
            }
        }

        val anyEmpty = board.any { it == ' ' }
        return if (anyEmpty) Outcome.InProgress else Outcome.Draw
    }
}
