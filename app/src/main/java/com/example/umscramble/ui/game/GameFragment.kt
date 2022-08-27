package com.example.umscramble.ui.game

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import com.example.umscramble.R
import com.example.umscramble.databinding.GameFragmentBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class GameFragment : Fragment() {
    //reference to ViewModel
    private val viewModel: GameViewModel by viewModels() /* Delegado de propiedad -> Transfiere responsabilidad
    del metodo get y set en una clase diferente*/

    private lateinit var binding: GameFragmentBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout XML file and return a binding object instance
        /*binding = GameFragmentBinding.inflate(inflater, container, false) // ==> Se
        reemplazó para la vinculacion de datos con la sig. linea: */
        binding = DataBindingUtil.inflate(inflater, R.layout.game_fragment, container, false)

        Log.d(TAG, "GameFragment created/re-created!")
        Log.d(TAG, "Word: ${viewModel.currentScrambledWord.value} " +
                        "Score: ${viewModel.score.value} " +
                        "WordCount: ${viewModel.currentWordCount.value}" )

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.gameViewModel = viewModel
        binding.maxNoOfWords = MAX_NO_OF_WORDS
        /* Specify the fragment view as the lifecycle owner of the binding.
        This is used so that the binding can observe LiveData updates */
        binding.lifecycleOwner = viewLifecycleOwner

        //Setup onClick listener for a submit and skip buttons
        binding.submit.setOnClickListener { onSubmitWord() }
        binding.skip.setOnClickListener { onSkipWord() }

        //Update the UI with Observers of LiveData to currentScrambledWord, currentWordCount and score
        // Se usa sin vinculacion de datos desde el diseño
        /*viewModel.currentScrambledWord.observe(viewLifecycleOwner) { newWord ->
            binding.textViewUnscrambleWord.text = newWord
        }
        viewModel.currentWordCount.observe( viewLifecycleOwner) { newWordCount ->
            binding.wordCount.text = getString(R.string.word_count, newWordCount, MAX_NO_OF_WORDS)
        }
        viewModel.score.observe(viewLifecycleOwner) { newScore ->
            binding.score.text = getString(R.id.score, newScore)
        } */
    }

    /*
            * Checks the user's word, and updates the score accordingly.
            * Displays the next scrambled word.
            */
    private fun onSubmitWord() {
        val playerWord = binding.textInputEditText.text.toString()
        clearValues()

        if ( viewModel.isUserWordCorrect(playerWord)) {
            setErrorTextField(false)
            if ( !viewModel.nextWord()){
                showFinalScoreDialog()
            }
        } else {
            setErrorTextField(true)
        }
    }

    /*
     * Skips the current word without changing the score.
     * Increases the word count.
     */
    private fun onSkipWord() {
        if ( viewModel.nextWord()){
            setErrorTextField(false)
            clearValues()
        } else {
            showFinalScoreDialog()
        }
    }

    //Crea y muestra el AlertDialog con los puntos finales
    private fun showFinalScoreDialog() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(getString(R.string.congratulations))
            .setMessage(getString(R.string.you_scored, viewModel.score.value))
            .setCancelable(false) //No se puede cancelar el dialog cuando se presione la tecla Back
            .setNegativeButton(getString(R.string.exit)) { _, _, -> // recibe dos parametros una string y una funcion
                exitGame()
            }
            .setPositiveButton(getString(R.string.play_again)) { _, _, ->
                restartGame()
            }
            .show()
    }

    /*
     * Re-initializes the data in the ViewModel and updates the views with the new data, to
     * restart the game.
     */
    private fun restartGame() {
        viewModel.reinitializeData()
        setErrorTextField(false)
    }

    /*
     * Exits the game.
     */
    private fun exitGame() {
        activity?.finish()
    }

    //Establece un error y resetea el texto
    private fun setErrorTextField(error: Boolean) {
        if (error){
            binding.textField.isErrorEnabled = true
            binding.textField.error = getString(R.string.try_again)
        } else {
            binding.textField.isErrorEnabled = false
            binding.textField.error = null
        }
    }

    private fun clearValues() {
        binding.textInputEditText.text?.clear()
    }
}