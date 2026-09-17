package com.example.mad_exp_7

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        // Adjust view padding for edge-to-edge display
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Sample list of places
        val samplePlaces = listOf(
            Place(
                id = 1,
                title = "Serene Mountain Valley",
                category = "Nature",
                snippet = "A breathtaking sight of lush emerald greenery...",
                description = "Nestled deep between majestic snowy peaks, the Serene Mountain Valley offers a pristine escape from the bustling world. It is filled with lush meadows, wildflower patches, crystal clear flowing alpine streams, and unparalleled hiking trails for adventurers.",
                drawableResId = R.drawable.bg_gradient_1
            ),
            Place(
                id = 2,
                title = "Cyberpunk Neo Metropolis",
                category = "Urban",
                snippet = "Glittering neon billboards towering high...",
                description = "Experience the pulse of tomorrow in the Neo Metropolis. Futuristic skyscrapers are draped in neon lights, cybernetic transit networks flow overhead, and diverse tech-hubs create a vibrant night-life filled with digital innovation and culture.",
                drawableResId = R.drawable.bg_gradient_2
            ),
            Place(
                id = 3,
                title = "Sun-Kissed Golden Coast",
                category = "Beaches",
                snippet = "Warm soothing ocean waves hitting clean sands...",
                description = "The Golden Coast is legendary for its long stretches of fine sand, world-class surfing waves, and iconic sunsets that light up the sky in shades of gold, amber, and deep orange. Perfect for absolute relaxation and aquatic sports.",
                drawableResId = R.drawable.bg_gradient_3
            ),
            Place(
                id = 4,
                title = "Mystical Autumnal Woods",
                category = "Forest",
                snippet = "Golden leaves carpeting quiet forest trails...",
                description = "A serene forest path where time seems to stand still. The Mystical Autumnal Woods are canopy-covered by ancient maple trees forever locked in rich, glowing shades of crimson, copper, and gold, whispering tales of old to travelers.",
                drawableResId = R.drawable.bg_gradient_4
            ),
            Place(
                id = 5,
                title = "Cosmic Stargazer Observatory",
                category = "Space",
                snippet = "Peer into the endless galaxies above...",
                description = "Perched high above the clouds on an isolated mountain peak, this state-of-the-art astronomical conservatory allows stargazers to clearly view stunning nebula formations, planet rings, and shooting stars across the cosmic void.",
                drawableResId = R.drawable.bg_gradient_5
            )
        )

        // Check if we are running in Dual-Pane mode (Tablets or landscape screen >= 600dp width)
        val detailCard = findViewById<View>(R.id.detailCard)
        val isDualPane = detailCard != null

        // If in dual pane mode, grab view handles and pre-populate fields
        var updateDetailPane: ((Place) -> Unit)? = null
        if (isDualPane) {
            val detailImage = findViewById<ImageView>(R.id.detailImage)
            val detailTitle = findViewById<TextView>(R.id.detailTitle)
            val detailCategory = findViewById<TextView>(R.id.detailCategory)
            val detailDescription = findViewById<TextView>(R.id.detailDescription)
            val btnAction = findViewById<MaterialButton>(R.id.btnAction)

            updateDetailPane = { place: Place ->
                detailImage?.setImageResource(place.drawableResId)
                detailTitle?.text = place.title
                detailCategory?.text = place.category
                detailDescription?.text = place.description
                btnAction?.text = "Discover Full Gallery"
                btnAction?.setOnClickListener {
                    Toast.makeText(this, "Opening full map for ${place.title}!", Toast.LENGTH_SHORT).show()
                }
            }

            // Default selection for side panel
            updateDetailPane(samplePlaces[0])
        }

        // Set up the high fidelity RecyclerView list
        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView)
        recyclerView.adapter = PlaceAdapter(samplePlaces) { selectedPlace ->
            if (isDualPane && updateDetailPane != null) {
                // Large device wide panel mode -> Update details side-by-side directly
                updateDetailPane(selectedPlace)
            } else {
                // Standard mobile single screen device mode -> Navigate to full View Page
                val intent = Intent(this, DetailActivity::class.java).apply {
                    putExtra("EXTRA_PLACE", selectedPlace)
                }
                startActivity(intent)
            }
        }
    }
}
