package com.example.app

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.bruise_treatment.*

class BruiseActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.bruise_treatment)

        // Return to main activity
        backButton.setOnClickListener {
            finish()
        }
    }
}