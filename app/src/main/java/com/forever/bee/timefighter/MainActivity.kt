/**
 * Android Programming With Kotlin
 *
 * @author Nguyen Truong Thinh
 * @since Kotlin 1.6 - JDK 11 (Java 11)
 *
 * Contact me: nguyentruongthinhvn2020@gmail.com || +84393280504
 * */
package com.forever.bee.timefighter

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.widget.Button
import android.widget.TextView
import android.widget.Toast

// import android.view.ViewGroup

@SuppressLint("StaticFieldLeak")
private lateinit var gameScoreTextView: TextView

@SuppressLint("StaticFieldLeak")
private lateinit var timeLeftTextView: TextView

@SuppressLint("StaticFieldLeak")
private lateinit var tapMeButton: Button

private lateinit var countDownTimer: CountDownTimer

private var score = 0
private var gameStarted = false
private var initialCountDown: Long = 60_000L
private var countDownInterval: Long = 1_000L
private var timeLeft = 60

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

//        testCash()
        hookingUpViews()
        tapMeEvent()
        resetGame()
    }

//    private fun testCash() {
//        val crashButton = Button(this)
//        crashButton.text = "Test Crash"
//        crashButton.setOnClickListener {
//            throw RuntimeException("Test Crash") // Force a crash
//        }
//
//        addContentView(
//            crashButton, ViewGroup.LayoutParams(
//                ViewGroup.LayoutParams.MATCH_PARENT,
//                ViewGroup.LayoutParams.WRAP_CONTENT
//            )
//        )
//    }

    private fun tapMeEvent() {
        tapMeButton.setOnClickListener { incrementScore() }
    }

    private fun hookingUpViews() {
        gameScoreTextView = findViewById(R.id.game_score_text_view)
        timeLeftTextView = findViewById(R.id.time_left_text_view)
        tapMeButton = findViewById(R.id.tap_me_button)
    }

    private fun incrementScore() {
        if (!gameStarted) {
            startGame()
        }

        score++

        //val newScore = "Your Score: $score"
        val newScore = getString(R.string.your_score, score)
        gameScoreTextView.text = newScore
    }

    private fun resetGame() {
        score = 0

        val initialScore = getString(R.string.your_score, score)
        gameScoreTextView.text = initialScore

        val initialTimeLeft = getString(R.string.time_left, 60)
        timeLeftTextView.text = initialTimeLeft

        countDownTimer = object : CountDownTimer(initialCountDown, countDownInterval) {
            override fun onTick(p0: Long) { // p0: millis until finished (countDownTimer that counts down to zero.).
                timeLeft = p0.toInt() / 1000

                val timeLeftString = getString(R.string.time_left, timeLeft)
                timeLeftTextView.text = timeLeftString
            }

            override fun onFinish() {
                endGame()
            }
        }

        gameStarted = false
    }

    private fun startGame() {
        countDownTimer.start()
        gameStarted = true
    }

    private fun endGame() {
        Toast
            .makeText(this, getString(R.string.game_over_message, score), Toast.LENGTH_LONG)
            .show()
        resetGame()
    }
}