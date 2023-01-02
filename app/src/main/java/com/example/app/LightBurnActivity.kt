package com.example.app

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.light_burn_treatment.*

class LightBurnActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.light_burn_treatment)

        // Return to main activity
        backButton.setOnClickListener {
            finish()
        }
    }
}