package com.example.app

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.skin_cut_treatment.*

class SkinCutActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.skin_cut_treatment)

        // Return to main activity
        backButton.setOnClickListener {
            finish()
        }
    }
}