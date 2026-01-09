package org.example.app

import android.animation.ArgbEvaluator
import android.animation.ValueAnimator
import android.app.Activity
import android.graphics.Typeface
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView

/**
 * App entrypoint Activity for the Tic Tac Toe game.
 *
 * Responsibilities:
 * - Inflate the XML layout
 * - Wire click listeners for the 3x3 board and controls
 * - Render state from [GameViewModel] into the UI
 */
class MainActivity : Activity() {

    private val viewModel = GameViewModel()

    private lateinit var statusText: TextView
    private lateinit var scoreXText: TextView
    private lateinit var scoreOText: TextView
    private lateinit var scoreDrawText: TextView
    private lateinit var restartButton: Button
    private lateinit var resetScoresButton: Button

    private lateinit var cells: List<Button>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        statusText = findViewById(R.id.statusText)
        scoreXText = findViewById(R.id.scoreXText)
        scoreOText = findViewById(R.id.scoreOText)
        scoreDrawText = findViewById(R.id.scoreDrawText)
        restartButton = findViewById(R.id.restartButton)
        resetScoresButton = findViewById(R.id.resetScoresButton)

        cells = listOf(
            findViewById(R.id.cell0),
            findViewById(R.id.cell1),
            findViewById(R.id.cell2),
            findViewById(R.id.cell3),
            findViewById(R.id.cell4),
            findViewById(R.id.cell5),
            findViewById(R.id.cell6),
            findViewById(R.id.cell7),
            findViewById(R.id.cell8),
        )

        cells.forEachIndexed { index, button ->
            button.setOnClickListener {
                viewModel.onCellTapped(index)
            }
        }

        restartButton.setOnClickListener {
            viewModel.restart(countScoreFromPreviousRound = true)
        }

        resetScoresButton.setOnClickListener {
            viewModel.resetScoresAndRestart()
        }

        viewModel.onStateChanged = { uiState ->
            render(uiState)
        }
    }

    private fun render(uiState: GameViewModel.UiState) {
        val state = uiState.state
        val scores = uiState.scores

        // Update board buttons.
        cells.forEachIndexed { index, button ->
            val symbol = state.board[index]
            button.text = if (symbol == ' ') "" else symbol.toString()

            // Disable moves if game is over or cell is already taken.
            button.isEnabled = (state.outcome is GameEngine.Outcome.InProgress) && symbol == ' '

            // Set a slightly bolder look for placed marks.
            button.setTypeface(button.typeface, if (symbol == ' ') Typeface.NORMAL else Typeface.BOLD)
            button.alpha = if (button.isEnabled) 1.0f else 0.92f
        }

        // Highlight winning line if any.
        clearHighlights()
        when (val outcome = state.outcome) {
            is GameEngine.Outcome.Win -> {
                outcome.line.forEach { idx ->
                    highlightCell(idx)
                }
            }
            else -> Unit
        }

        statusText.text = when (val outcome = state.outcome) {
            is GameEngine.Outcome.InProgress -> "${state.currentPlayer.symbol}'s turn"
            is GameEngine.Outcome.Win -> "${outcome.winner.symbol} wins!"
            is GameEngine.Outcome.Draw -> "Draw"
        }

        scoreXText.text = scores.xWins.toString()
        scoreOText.text = scores.oWins.toString()
        scoreDrawText.text = scores.draws.toString()

        // Smoothly indicate game-over by enabling restart emphasis.
        restartButton.isEnabled = true
        animateRestartButton(state.outcome !is GameEngine.Outcome.InProgress)
    }

    private fun clearHighlights() {
        for (button in cells) {
            button.isSelected = false
        }
    }

    private fun highlightCell(index: Int) {
        val b = cells[index]
        b.isSelected = true
    }

    private fun animateRestartButton(gameOver: Boolean) {
        // Very lightweight "smooth transition": animate alpha when gameOver toggles.
        val targetAlpha = if (gameOver) 1.0f else 0.95f
        if (restartButton.alpha == targetAlpha) return

        val animator = ValueAnimator.ofFloat(restartButton.alpha, targetAlpha)
        animator.duration = 180L
        animator.addUpdateListener { a ->
            restartButton.alpha = a.animatedValue as Float
        }
        animator.start()

        // Also animate text color slightly for emphasis (primary when over, normal otherwise)
        val normalColor = getColorCompat(R.color.ocean_text)
        val accentColor = getColorCompat(R.color.ocean_primary)

        val from = (restartButton.tag as? Int) ?: normalColor
        val to = if (gameOver) accentColor else normalColor
        restartButton.tag = to

        val colorAnim = ValueAnimator.ofObject(ArgbEvaluator(), from, to)
        colorAnim.duration = 180L
        colorAnim.addUpdateListener { a ->
            restartButton.setTextColor(a.animatedValue as Int)
        }
        colorAnim.start()
    }

    @Suppress("DEPRECATION")
    private fun getColorCompat(colorRes: Int): Int {
        return resources.getColor(colorRes)
    }
}
