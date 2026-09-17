package com.example.mad_exp_7

import android.os.Bundle
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.button.MaterialButton

class DetailActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_detail)

        // Adjust for Edge-to-Edge
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.detailMain)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Get the place object from Intent
        val place = intent.getSerializableExtra("EXTRA_PLACE") as? Place

        if (place == null) {
            finish()
            return
        }

        // Bind Views
        val btnBack = findViewById<ImageButton>(R.id.btnBack)
        val tvBarTitle = findViewById<TextView>(R.id.tvBarTitle)
        val detailImage = findViewById<ImageView>(R.id.detailImage)
        val detailTitle = findViewById<TextView>(R.id.detailTitle)
        val detailCategory = findViewById<TextView>(R.id.detailCategory)
        val detailDescription = findViewById<TextView>(R.id.detailDescription)
        val btnAction = findViewById<MaterialButton>(R.id.btnAction)

        // Populate Content
        tvBarTitle.text = place.title
        detailImage.setImageResource(place.drawableResId)
        detailTitle.text = place.title
        detailCategory.text = place.category
        detailDescription.text = place.description
        btnAction.text = "Start Journey to ${place.title}"

        // Set Listeners
        btnBack.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        btnAction.setOnClickListener {
            Toast.makeText(this, "Beginning travel plan for ${place.title}...", Toast.LENGTH_SHORT).show()
        }
    }
}
