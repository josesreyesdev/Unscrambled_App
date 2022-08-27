package com.example.umscramble.ui.game

import android.text.Spannable
import android.text.SpannableString
import android.text.style.TtsSpan
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel

const val TAG = "GameFragment"
class GameViewModel: ViewModel() {

    private val _score = MutableLiveData(0)
    val score: LiveData<Int> get() = _score

    private val _currentWordCount = MutableLiveData(0)
    val currentWordCount: LiveData<Int> get() = _currentWordCount

    private val _currentScrambledWord = MutableLiveData<String>()
    // set the current scrambled word para leer el talkback con spannable para separar un straing a caracteres
    val currentScrambledWord: LiveData<Spannable> = Transformations.map(_currentScrambledWord) {
        if (it == null) {
            SpannableString("")
        } else {
            val scrambledWord = it.toString()
            val spannable: Spannable = SpannableString(scrambledWord)
            spannable.setSpan(
                TtsSpan.VerbatimBuilder(scrambledWord).build(),
                0,
                scrambledWord.length,
                Spannable.SPAN_INCLUSIVE_INCLUSIVE
            )
            spannable
        }
    }
        //get() = _currentScrambledWord

    private lateinit var currentWord: String
    var wordList: MutableList<String> = mutableListOf()


    /*Get next Word => Update la currentWord y
        currentScrambledWord con la sig word  */
    private fun getNextWord() {
        currentWord = allWordsList.random()
        Log.d("GameFragment", "Current Word: $currentWord")
        val tempWord = currentWord.toCharArray()
        tempWord.shuffle()

        while (String(tempWord).equals(currentWord, false)) {
            tempWord.shuffle()
        }
        if (wordList.contains(currentWord)) {
            getNextWord()
        } else {
            _currentScrambledWord.value = String(tempWord)
            _currentWordCount.value = (_currentWordCount.value)?.inc() //aumenta el valor en uno
            wordList.add(currentWord)
        }
    }

    init {
        Log.d(TAG, "GameViewModel created!!")
        getNextWord()
    }

    /* re-initialize the game data to restart the game */
    fun reinitializeData() {
        _score.value = 0
        _currentWordCount.value = 0
        wordList.clear()
        getNextWord()
    }

    private fun increaseScore() {
        _score.value = (_score.value)?.plus(SCORE_INCREASE)
    }

    fun isUserWordCorrect( playerWord: String): Boolean {
        return if ( playerWord.equals(currentWord, true)) {
            increaseScore()
            true
        } else false
    }

    fun nextWord(): Boolean {
        return if ( _currentWordCount.value!! < MAX_NO_OF_WORDS) {
            getNextWord()
            true
        } else false
    }

}