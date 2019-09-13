package io.github.birdsea

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val scaleTextView = findViewById<ScaleTextView>(R.id.scaleTextView)

        scaleTextView.text = "Hello World\n\nPlease pinch-in/out"
        scaleTextView.minScale = 1.0f
        scaleTextView.maxScale = 3.0f
    }
}
