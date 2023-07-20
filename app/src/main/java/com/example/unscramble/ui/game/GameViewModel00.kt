package com.example.unscramble.ui.game

import android.util.Log
import androidx.lifecycle.ViewModel

private const val TAG00 = "GameViewModel"
class GameViewModel00 : ViewModel() {

    private var _score = 0 //puntos
    val count: Int get() = _score

    private var _currentWordCount = 0 //contador de palabra actual
    val currentWordCount: Int get() = _currentWordCount

    private lateinit var _currentScrambledWord: String //Palabra desordenada actual
    val currentScrambledWord: String get() = _currentScrambledWord

    private lateinit var currentWord: String //palabra actual a descifrar
    private var wordList: MutableList<String> = mutableListOf() // listaPalabrasUsadas

    private fun getNextWord() {
        currentWord = allWordsList.random()
        val temporalWord = currentWord.toCharArray()
        temporalWord.shuffle()

        while ( String(temporalWord).equals(currentWord, false)) {
            temporalWord.shuffle()
        }

        if (wordList.contains(currentWord)) {
            getNextWord()
        } else {
            _currentScrambledWord = String(temporalWord)
            ++_currentWordCount
            wordList.add(currentWord)
        }
    }

    init {
        Log.d(TAG00, "Game ViewModel Created!")
        getNextWord()
    }

    //Metodo de ayuda para procesar y modificar datos
    fun nextWord(): Boolean {
        return if(_currentWordCount < MAX_NO_OF_WORDS) {
            getNextWord()
            true
        } else false
    }

    //Incrementar puntaje
    private fun increaseScore() {
        _score += SCORE_INCREASE
    }

    fun isUserCorrect( playerWord: String): Boolean {
        if (playerWord.equals(currentWord, true)) {
            increaseScore()
            return true
        }
        return false
    }

    //Reinicializar datos
    private fun reinitializeData() {
        _score = 0
        _currentWordCount = 0
        wordList.clear()
        getNextWord()
    }
}