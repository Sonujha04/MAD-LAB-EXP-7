# Adaptive Places Explorer - Comprehensive Android Application

A premium, responsive, and adaptive Android native application built using **Kotlin**, **XML Layouts with ConstraintLayout**, and **Material 3 Design Tokens**. It implements a high-fidelity list-to-detail navigation flow that alters its architectural layout pattern automatically across phones, foldables, and large screen tablets.

---

## 📖 Table of Contents
1. [Project Overview](#-project-overview)
2. [Key Architecture & Features](#-key-architecture--features)
3. [Project Directory Structure](#-project-directory-structure)
4. [Complete Source Code Files](#-complete-source-code-files)
   - [Place.kt (Data Schema Model)](#1-placekt-data-schema-model)
   - [PlaceAdapter.kt (RecyclerView List Bridge)](#2-placeadapterkt-recyclerview-list-bridge)
   - [MainActivity.kt (Main App Controller & Smart Router)](#3-mainactivitykt-main-app-controller--smart-router)
   - [DetailActivity.kt (Standalone View Page Screen)](#4-detailactivitykt-standalone-view-page-screen)
5. [Complete Resource Layouts & Custom Styles](#-complete-resource-layouts--custom-styles)
   - [AndroidManifest.xml](#1-androidmanifestxml)
   - [activity_main.xml (Mobile Screen)](#2-activity_mainxml-mobile-screen)
   - [activity_main.xml (Widescreen Tablet Mode)](#3-activity_mainxml-widescreen-tablet-mode)
   - [activity_detail.xml (Standalone View Page Layout)](#4-activity_detailxml-standalone-view-page-layout)
   - [item_place.xml (Custom Row Card Item)](#5-item_placexml-custom-row-card-item)
   - [colors.xml (Material 3 Theme Palette)](#6-colorsxml-material-3-theme-palette)
   - [bg_tag.xml & Vector Back Icons](#7-bg_tagxml--vector-back-icons)
6. [Adaptive Routing Logic Explained](#-adaptive-routing-logic-explained)
7. [Installation & Operational Guide](#-installation--operational-guide)

---

## 🌟 Project Overview

The **Adaptive Places Explorer** demonstrates real-world application screen scaling best practices. Instead of building separate code paths or single-target displays, this app relies on system layout handle markers to choose the best configuration:
- **On Standard Smartphones (Portrait):** Displays a polished, dedicated full-screen list. Tapping any item initiates an explicit intent transaction, loading an immersive, standalone **View Detail Page** screen complete with smooth sliding animations, a stylized top bar, and rich context content layout structures.
- **On Large Screens / Tablets / Landscape (`w600dp`):** Switches immediately to an integrated, split-screen dashboard view. The list scrolls on the left, and clicking items updates a detailed workspace image and metadata sheet on the right layout surface directly, eliminating unnecessary screen travel.

---

## ✨ Key Architecture & Features

*   **Material 3 System Components:** Employs rounded container tokens, borderless custom `MaterialCardView` layout sheets, padding margins, and high-contrast typography scaling.
*   **Edge-to-Edge Experience:** Incorporates window padding listeners via `ViewCompat.setOnApplyWindowInsetsListener` to blend active headers into system bars.
*   **Vector Shape-Defined Canvas Art:** Implements 5 vibrant custom linear color gradient drawables (`bg_gradient_1..5.xml`) as placeholder graphics to save device package size without sacrificing styling.
*   **Serializable Data Serialization:** Implements a decoupled structure, letting complex custom travel destination metrics shift cleanly over activity transaction boundaries.

---

## 📂 Project Directory Structure

```
madexp7/
│
├── app/src/main/java/com/example/mad_exp_7/
│   ├── Place.kt               # Serializable data model schema.
│   ├── PlaceAdapter.kt        # RecyclerView Adapter binding model grids to view cells.
│   ├── MainActivity.kt        # Primary orchestrator and smart navigation adapter.
│   └── DetailActivity.kt      # Standalone View Page screen controller for single-pane devices.
│
└── app/src/main/res/
    ├── layout/
    │   ├── activity_main.xml   # Full screen list configuration (Smartphones).
    │   ├── activity_detail.xml # Immersive View Page layout (Smartphone view state).
    │   └── item_place.xml      # Highly tailored card template row for places list items.
    ├── layout-w600dp/
    │   └── activity_main.xml   # Integrated side-by-side split configuration (Tablets/Landscape).
    ├── drawable/
    │   ├── bg_gradient_1..5.xml# Color canvas vector backgrounds.
    │   ├── bg_tag.xml          # Rounded item category tag badge filter background.
    │   └── ic_arrow_back.xml   # High resolution custom arrow back navigation vector.
    └── values/
        ├── colors.xml          # Unified Material 3 color definition keys.
        └── themes.xml          # Core Application styling values.
```

---

## 💻 Complete Source Code Files

### 1. `Place.kt` (Data Schema Model)
Located at: `app/src/main/java/com/example/mad_exp_7/Place.kt`
```kotlin
package com.example.mad_exp_7

import java.io.Serializable

data class Place(
    val id: Int,
    val title: String,
    val category: String,
    val snippet: String,
    val description: String,
    val drawableResId: Int
) : Serializable
```

### 2. `PlaceAdapter.kt` (RecyclerView List Bridge)
Located at: `app/src/main/java/com/example/mad_exp_7/PlaceAdapter.kt`
```kotlin
package com.example.mad_exp_7

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class PlaceAdapter(
    private val places: List<Place>,
    private val onItemClick: (Place) -> Unit
) : RecyclerView.Adapter<PlaceAdapter.PlaceViewHolder>() {

    class PlaceViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val thumbnail: ImageView = view.findViewById(R.id.itemThumbnail)
        val title: TextView = view.findViewById(R.id.itemTitle)
        val category: TextView = view.findViewById(R.id.itemCategory)
        val snippet: TextView = view.findViewById(R.id.itemSnippet)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlaceViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_place, parent, false)
        return PlaceViewHolder(view)
    }

    override fun onBindViewHolder(holder: PlaceViewHolder, position: Int) {
        val place = places[position]
        holder.title.text = place.title
        holder.category.text = place.category
        holder.snippet.text = place.snippet
        holder.thumbnail.setImageResource(place.drawableResId)

        holder.itemView.setOnClickListener {
            onItemClick(place)
        }
    }

    override fun getItemCount(): Int = places.size
}
```

### 3. `MainActivity.kt` (Main App Controller & Smart Router)
Located at: `app/src/main/java/com/example/mad_exp_7/MainActivity.kt`
```kotlin
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

        // Sample list of places with pre-configured color vector drawing hooks
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
```

### 4. `DetailActivity.kt` (Standalone View Page Screen)
Located at: `app/src/main/java/com/example/mad_exp_7/DetailActivity.kt`
```kotlin
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

        // Adjust for Edge-to-Edge system padding configuration
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.detailMain)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Extract passed explicit data payload
        val place = intent.getSerializableExtra("EXTRA_PLACE") as? Place

        if (place == null) {
            finish()
            return
        }

        // Bind layout IDs
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

        // Custom Top Back Navigation Trigger
        btnBack.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        btnAction.setOnClickListener {
            Toast.makeText(this, "Beginning travel plan for ${place.title}...", Toast.LENGTH_SHORT).show()
        }
    }
}
```

---

## 🎨 Complete Resource Layouts & Custom Styles

### 1. `AndroidManifest.xml`
Located at: `app/src/main/AndroidManifest.xml`
```xml
<?xml version="1.0" encoding="utf-8"?>
<manifest xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:tools="http://schemas.android.com/tools">

    <application
        android:allowBackup="true"
        android:dataExtractionRules="@xml/data_extraction_rules"
        android:fullBackupContent="@xml/backup_rules"
        android:icon="@mipmap/ic_launcher"
        android:label="Places Explorer"
        android:roundIcon="@mipmap/ic_launcher_round"
        android:supportsRtl="true"
        android:theme="@style/Theme.Madexp7">
        
        <activity
            android:name=".MainActivity"
            android:exported="true"
            android:windowSoftInputMode="adjustResize">
            <intent-filter>
                <action android:name="android.intent.action.MAIN" />
                <category android:name="android.intent.category.LAUNCHER" />
            </intent-filter>
        </activity>
        
        <activity
            android:name=".DetailActivity"
            android:exported="false" />
            
    </application>

</manifest>
```

### 2. `activity_main.xml` (Mobile Screen Layout)
Located at: `app/src/main/res/layout/activity_main.xml`
```xml
<?xml version="1.0" encoding="utf-8"?>
<androidx.constraintlayout.widget.ConstraintLayout xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    xmlns:tools="http://schemas.android.com/tools"
    android:id="@+id/main"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:background="@color/background"
    tools:context=".MainActivity">

    <TextView
        android:id="@+id/appTitle"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:paddingHorizontal="16dp"
        android:paddingTop="20dp"
        android:paddingBottom="12dp"
        android:text="Explore Beautiful Places"
        android:textColor="@color/primary"
        android:textSize="24sp"
        android:textStyle="bold"
        app:layout_constraintTop_toTopOf="parent"
        app:layout_constraintStart_toStartOf="parent"/>

    <androidx.recyclerview.widget.RecyclerView
        android:id="@+id/recyclerView"
        android:layout_width="match_parent"
        android:layout_height="0dp"
        android:clipToPadding="false"
        android:paddingBottom="16dp"
        app:layoutManager="androidx.recyclerview.widget.LinearLayoutManager"
        app:layout_constraintBottom_toBottomOf="parent"
        app:layout_constraintEnd_toEndOf="parent"
        app:layout_constraintStart_toStartOf="parent"
        app:layout_constraintTop_toBottomOf="@id/appTitle"
        tools:listitem="@layout/item_place" />

</androidx.constraintlayout.widget.ConstraintLayout>
```

### 3. `activity_main.xml` (Widescreen Tablet Layout Modifiers)
Located at: `app/src/main/res/layout-w600dp/activity_main.xml`
```xml
<?xml version="1.0" encoding="utf-8"?>
<androidx.constraintlayout.widget.ConstraintLayout xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    xmlns:tools="http://schemas.android.com/tools"
    android:id="@+id/main"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:background="@color/background"
    tools:context=".MainActivity">

    <TextView
        android:id="@+id/appTitle"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:paddingHorizontal="24dp"
        android:paddingTop="20dp"
        android:paddingBottom="12dp"
        android:text="Explore Beautiful Places (Adaptive Large Mode)"
        android:textColor="@color/primary"
        android:textSize="26sp"
        android:textStyle="bold"
        app:layout_constraintTop_toTopOf="parent"
        app:layout_constraintStart_toStartOf="parent"/>

    <androidx.constraintlayout.widget.Guideline
        android:id="@+id/splitGuideline"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:orientation="vertical"
        app:layout_constraintGuide_percent="0.38" />

    <androidx.recyclerview.widget.RecyclerView
        android:id="@+id/recyclerView"
        android:layout_width="0dp"
        android:layout_height="0dp"
        android:clipToPadding="false"
        android:paddingBottom="16dp"
        app:layoutManager="androidx.recyclerview.widget.LinearLayoutManager"
        app:layout_constraintBottom_toBottomOf="parent"
        app:layout_constraintEnd_toStartOf="@id/splitGuideline"
        app:layout_constraintStart_toStartOf="parent"
        app:layout_constraintTop_toBottomOf="@id/appTitle"
        tools:listitem="@layout/item_place" />

    <View
        android:id="@+id/divider"
        android:layout_width="1dp"
        android:layout_height="0dp"
        android:background="@color/surface_variant"
        app:layout_constraintStart_toEndOf="@id/splitGuideline"
        app:layout_constraintTop_toBottomOf="@id/appTitle"
        app:layout_constraintBottom_toBottomOf="parent" />

    <com.google.android.material.card.MaterialCardView
        android:id="@+id/detailCard"
        android:layout_width="0dp"
        android:layout_height="0dp"
        android:layout_margin="20dp"
        app:cardCornerRadius="24dp"
        app:cardElevation="2dp"
        app:strokeWidth="0dp"
        app:cardBackgroundColor="@color/surface"
        app:layout_constraintBottom_toBottomOf="parent"
        app:layout_constraintEnd_toEndOf="parent"
        app:layout_constraintStart_toEndOf="@id/divider"
        app:layout_constraintTop_toBottomOf="@id/appTitle">

        <androidx.core.widget.NestedScrollView
            android:layout_width="match_parent"
            android:layout_height="match_parent"
            android:fillViewport="true">

            <androidx.constraintlayout.widget.ConstraintLayout
                android:layout_width="match_parent"
                android:layout_height="wrap_content"
                android:padding="24dp">

                <ImageView
                    android:id="@+id/detailImage"
                    android:layout_width="match_parent"
                    android:layout_height="260dp"
                    android:scaleType="centerCrop"
                    android:background="@drawable/bg_gradient_1"
                    android:contentDescription="Detailed image view"
                    app:layout_constraintTop_toTopOf="parent"
                    app:layout_constraintStart_toStartOf="parent"
                    app:layout_constraintEnd_toEndOf="parent" />

                <TextView
                    android:id="@+id/detailTitle"
                    android:layout_width="0dp"
                    android:layout_height="wrap_content"
                    android:layout_marginTop="20dp"
                    android:text="Select a Place"
                    android:textColor="@color/on_background"
                    android:textSize="28sp"
                    android:textStyle="bold"
                    app:layout_constraintTop_toBottomOf="@id/detailImage"
                    app:layout_constraintStart_toStartOf="parent"
                    app:layout_constraintEnd_toEndOf="parent" />

                <TextView
                    android:id="@+id/detailCategory"
                    android:layout_width="wrap_content"
                    android:layout_height="wrap_content"
                    android:layout_marginTop="8dp"
                    android:background="@drawable/bg_tag"
                    android:paddingHorizontal="12dp"
                    android:paddingVertical="6dp"
                    android:text="Explore"
                    android:textColor="@color/primary"
                    android:textSize="13sp"
                    android:textStyle="bold"
                    app:layout_constraintTop_toBottomOf="@id/detailTitle"
                    app:layout_constraintStart_toStartOf="parent" />

                <TextView
                    android:id="@+id/detailDescription"
                    android:layout_width="0dp"
                    android:layout_height="wrap_content"
                    android:layout_marginTop="16dp"
                    android:text="Click any item in the left list to see detailed descriptions."
                    android:textColor="@color/on_surface_variant"
                    android:textSize="16sp"
                    android:lineSpacingMultiplier="1.3"
                    app:layout_constraintTop_toBottomOf="@id/detailCategory"
                    app:layout_constraintStart_toStartOf="parent"
                    app:layout_constraintEnd_toEndOf="parent" />

                <com.google.android.material.button.MaterialButton
                    android:id="@+id/btnAction"
                    android:layout_width="wrap_content"
                    android:layout_height="wrap_content"
                    android:layout_marginTop="32dp"
                    android:text="Discover Full Gallery"
                    android:paddingHorizontal="32dp"
                    android:paddingVertical="12dp"
                    app:cornerRadius="16dp"
                    app:layout_constraintTop_toBottomOf="@id/detailDescription"
                    app:layout_constraintStart_toStartOf="parent"/>

            </androidx.constraintlayout.widget.ConstraintLayout>
        </androidx.core.widget.NestedScrollView>
    </com.google.android.material.card.MaterialCardView>

</androidx.constraintlayout.widget.ConstraintLayout>
```

### 4. `activity_detail.xml` (Standalone View Page Layout)
Located at: `app/src/main/res/layout/activity_detail.xml`
```xml
<?xml version="1.0" encoding="utf-8"?>
<androidx.constraintlayout.widget.ConstraintLayout xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    xmlns:tools="http://schemas.android.com/tools"
    android:id="@+id/detailMain"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:background="@color/background"
    tools:context=".DetailActivity">

    <androidx.constraintlayout.widget.ConstraintLayout
        android:id="@+id/topBar"
        android:layout_width="match_parent"
        android:layout_height="56dp"
        android:paddingHorizontal="8dp"
        app:layout_constraintTop_toTopOf="parent"
        app:layout_constraintStart_toStartOf="parent"
        app:layout_constraintEnd_toEndOf="parent">

        <ImageButton
            android:id="@+id/btnBack"
            android:layout_width="48dp"
            android:layout_height="48dp"
            android:background="?attr/selectableItemBackgroundBorderless"
            android:contentDescription="Go Back"
            android:src="@drawable/ic_arrow_back"
            app:layout_constraintBottom_toBottomOf="parent"
            app:layout_constraintStart_toStartOf="parent"
            app:layout_constraintTop_toTopOf="parent"
            app:tint="@color/primary" />

        <TextView
            android:id="@+id/tvBarTitle"
            android:layout_width="0dp"
            android:layout_height="wrap_content"
            android:layout_marginStart="16dp"
            android:text="Details"
            android:textColor="@color/on_background"
            android:textSize="18sp"
            android:textStyle="bold"
            app:layout_constraintBottom_toBottomOf="parent"
            app:layout_constraintEnd_toEndOf="parent"
            app:layout_constraintStart_toEndOf="@id/btnBack"
            app:layout_constraintTop_toTopOf="parent" />

    </androidx.constraintlayout.widget.ConstraintLayout>

    <androidx.core.widget.NestedScrollView
        android:layout_width="match_parent"
        android:layout_height="0dp"
        android:fillViewport="true"
        app:layout_constraintTop_toBottomOf="@id/topBar"
        app:layout_constraintBottom_toBottomOf="parent">

        <androidx.constraintlayout.widget.ConstraintLayout
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:padding="20dp">

            <com.google.android.material.card.MaterialCardView
                android:id="@+id/imageCard"
                android:layout_width="match_parent"
                android:layout_height="240dp"
                app:cardCornerRadius="24dp"
                app:cardElevation="4dp"
                app:strokeWidth="0dp"
                app:layout_constraintTop_toTopOf="parent"
                app:layout_constraintStart_toStartOf="parent"
                app:layout_constraintEnd_toEndOf="parent">

                <ImageView
                    android:id="@+id/detailImage"
                    android:layout_width="match_parent"
                    android:layout_height="match_parent"
                    android:scaleType="centerCrop"
                    android:contentDescription="Place Image View" />
            </com.google.android.material.card.MaterialCardView>

            <TextView
                android:id="@+id/detailTitle"
                android:layout_width="0dp"
                android:layout_height="wrap_content"
                android:layout_marginTop="24dp"
                android:text="Place Title"
                android:textColor="@color/on_background"
                android:textSize="26sp"
                android:textStyle="bold"
                app:layout_constraintTop_toBottomOf="@id/imageCard"
                app:layout_constraintStart_toStartOf="parent"
                app:layout_constraintEnd_toEndOf="parent" />

            <TextView
                android:id="@+id/detailCategory"
                android:layout_width="wrap_content"
                android:layout_height="wrap_content"
                android:layout_marginTop="8dp"
                android:background="@drawable/bg_tag"
                android:paddingHorizontal="14dp"
                android:paddingVertical="6dp"
                android:text="Category"
                android:textColor="@color/primary"
                android:textSize="13sp"
                android:textStyle="bold"
                app:layout_constraintTop_toBottomOf="@id/detailTitle"
                app:layout_constraintStart_toStartOf="parent" />

            <View
                android:id="@+id/dividerLine"
                android:layout_width="match_parent"
                android:layout_height="1dp"
                android:layout_marginTop="16dp"
                android:background="@color/surface_variant"
                app:layout_constraintTop_toBottomOf="@id/detailCategory" />

            <TextView
                android:id="@+id/detailDescription"
                android:layout_width="0dp"
                android:layout_height="wrap_content"
                android:layout_marginTop="16dp"
                android:text="Full detailed place descriptions will be shown here..."
                android:textColor="@color/on_surface_variant"
                android:textSize="16sp"
                android:lineSpacingMultiplier="1.3"
                app:layout_constraintTop_toBottomOf="@id/dividerLine"
                app:layout_constraintStart_toStartOf="parent"
                app:layout_constraintEnd_toEndOf="parent" />

            <com.google.android.material.button.MaterialButton
                android:id="@+id/btnAction"
                android:layout_width="match_parent"
                android:layout_height="wrap_content"
                android:layout_marginTop="32dp"
                android:layout_marginBottom="24dp"
                android:text="Book an Exploration Tour"
                android:paddingVertical="12dp"
                app:cornerRadius="16dp"
                app:layout_constraintTop_toBottomOf="@id/detailDescription"
                app:layout_constraintBottom_toBottomOf="parent"
                app:layout_constraintStart_toStartOf="parent"
                app:layout_constraintEnd_toEndOf="parent"/>

        </androidx.constraintlayout.widget.ConstraintLayout>
    </androidx.core.widget.NestedScrollView>

</androidx.constraintlayout.widget.ConstraintLayout>
```

### 5. `item_place.xml` (Custom Row Card Item)
Located at: `app/src/main/res/layout/item_place.xml`
```xml
<?xml version="1.0" encoding="utf-8"?>
<com.google.android.material.card.MaterialCardView xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    android:id="@+id/cardView"
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    android:layout_marginHorizontal="12dp"
    android:layout_marginVertical="6dp"
    app:cardCornerRadius="16dp"
    app:cardElevation="0dp"
    app:strokeWidth="1dp"
    app:strokeColor="@color/surface_variant"
    app:cardBackgroundColor="@color/surface">

    <androidx.constraintlayout.widget.ConstraintLayout
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:padding="12dp">

        <ImageView
            android:id="@+id/itemThumbnail"
            android:layout_width="72dp"
            android:layout_height="72dp"
            android:scaleType="centerCrop"
            android:contentDescription="Thumbnail"
            app:layout_constraintStart_toStartOf="parent"
            app:layout_constraintTop_toTopOf="parent"
            app:layout_constraintBottom_toBottomOf="parent" />

        <TextView
            android:id="@+id/itemTitle"
            android:layout_width="0dp"
            android:layout_height="wrap_content"
            android:layout_marginStart="16dp"
            android:text="Place Title"
            android:textColor="@color/on_background"
            android:textSize="16sp"
            android:textStyle="bold"
            app:layout_constraintEnd_toEndOf="parent"
            app:layout_constraintStart_toEndOf="@id/itemThumbnail"
            app:layout_constraintTop_toTopOf="parent" />

        <TextView
            android:id="@+id/itemCategory"
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:layout_marginStart="16dp"
            android:layout_marginTop="4dp"
            android:background="@drawable/bg_tag"
            android:paddingHorizontal="8dp"
            android:paddingVertical="2dp"
            android:text="Category"
            android:textColor="@color/primary"
            android:textSize="11sp"
            android:textStyle="bold"
            app:layout_constraintStart_toEndOf="@id/itemThumbnail"
            app:layout_constraintTop_toBottomOf="@id/itemTitle" />

        <TextView
            android:id="@+id/itemSnippet"
            android:layout_width="0dp"
            android:layout_height="wrap_content"
            android:layout_marginStart="16dp"
            android:layout_marginTop="4dp"
            android:text="Short snippet of the place description goes here..."
            android:textColor="@color/on_surface_variant"
            android:textSize="13sp"
            android:maxLines="1"
            android:ellipsize="end"
            app:layout_constraintBottom_toBottomOf="parent"
            app:layout_constraintEnd_toEndOf="parent"
            app:layout_constraintStart_toEndOf="@id/itemThumbnail"
            app:layout_constraintTop_toBottomOf="@id/itemCategory" />

    </androidx.constraintlayout.widget.ConstraintLayout>
</com.google.android.material.card.MaterialCardView>
```

### 6. `colors.xml` (Material 3 Theme Palette)
Located at: `app/src/main/res/values/colors.xml`
```xml
<?xml version="1.0" encoding="utf-8"?>
<resources>
    <color name="black">#FF000000</color>
    <color name="white">#FFFFFFFF</color>
    
    <color name="primary">#6750A4</color>
    <color name="primary_container">#EADDFF</color>
    <color name="secondary">#625B71</color>
    <color name="secondary_container">#E8DEF8</color>
    <color name="background">#FEF7FF</color>
    <color name="surface">#F7F2FA</color>
    <color name="surface_variant">#E7E0EC</color>
    <color name="on_primary">#FFFFFF</color>
    <color name="on_primary_container">#21005D</color>
    <color name="on_background">#1D1B20</color>
    <color name="on_surface">#1D1B20</color>
    <color name="on_surface_variant">#49454F</color>
    
    <color name="gradient_1_start">#FF7E5F</color>
    <color name="gradient_1_end">#FEB47B</color>
    <color name="gradient_2_start">#2193B0</color>
    <color name="gradient_2_end">#6DD5ED</color>
    <color name="gradient_3_start">#11998E</color>
    <color name="gradient_3_end">#38EF7D</color>
    <color name="gradient_4_start">#7F00FF</color>
    <color name="gradient_4_end">#E100FF</color>
    <color name="gradient_5_start">#FF416C</color>
    <color name="gradient_5_end">#FF4B2B</color>
</resources>
```

### 7. `bg_tag.xml` & Vector Back Icons
Tag Pill drawable container (`app/src/main/res/drawable/bg_tag.xml`):
```xml
<?xml version="1.0" encoding="utf-8"?>
<shape xmlns:android="http://schemas.android.com/apk/res/android"
    android:shape="rectangle">
    <solid android:color="@color/secondary_container" />
    <corners android:radius="12dp" />
</shape>
```
Back arrow navigation asset (`app/src/main/res/drawable/ic_arrow_back.xml`):
```xml
<vector xmlns:android="http://schemas.android.com/apk/res/android"
    android:width="24dp"
    android:height="24dp"
    android:viewportWidth="24"
    android:viewportHeight="24">
  <path
      android:fillColor="#FF000000"
      android:pathData="M20,11H7.83l5.59,-5.59L12,4l-8,8 8,8 1.41,-1.41L7.83,13H20v-2z"/>
</vector>
```

---

## 🛠️ Adaptive Routing Logic Explained

The adaptive navigation flow is entirely orchestrated in `MainActivity.kt`. Rather than relying on fragile hardware size checks, the controller checks whether the view handle layout engine has unpacked the side panel card module resource:

```kotlin
// Check if the current layout grid contains the detail preview card pane container
val detailCard = findViewById<View>(R.id.detailCard)
val isDualPane = detailCard != null

// Establish smart adapter callback routing pathways
recyclerView.adapter = PlaceAdapter(samplePlaces) { selectedPlace ->
    if (isDualPane && updateDetailPane != null) {
        // SCENARIO A: Wide configuration (Tablets) -> Update the detailed side sheet immediately
        updateDetailPane(selectedPlace)
    } else {
        // SCENARIO B: Compact configuration (Smartphones) -> Route user to separate View Page activity
        val intent = Intent(this, DetailActivity::class.java).apply {
            putExtra("EXTRA_PLACE", selectedPlace)
        }
        startActivity(intent)
    }
}
```

---

## 🚀 Installation & Operational Guide

1.  Open the project in **Android Studio**.
2.  Connect a physical Android handset or start an Emulator instance.
3.  Click the green **Run Button (`▶`)** on the main menu bar or press `Shift + F10` on your keyboard.
4.  **Verifying Phone Navigation Flow:**
    * Run the application on a portrait screen smartphone device.
    * Click any item in the list layout. The device shifts screens completely, loading a separate view detail page activity. Tap the top back icon (`←`) to return.
5.  **Verifying Widescreen Dual-Pane Flow:**
    * Rotate the running mobile emulator into **Landscape** mode (`Ctrl + F11`), or deploy it directly on an **Android Tablet Grid**.
    * The UI morphs into a dual-pane view layout structure. Tapping any item now updates the right panel in real time without navigating away from the current screen.
## screenshots 
<img width="357" height="798" alt="Screenshot 2026-09-17 104900" src="https://github.com/user-attachments/assets/266ee762-9deb-45d0-a4d7-c889a5b675a1" />
<img width="361" height="785" alt="Screenshot 2026-09-17 104915" src="https://github.com/user-attachments/assets/c2ac649c-7932-40b7-a7e1-98c30dc05172" />
<img width="351" height="783" alt="Screenshot 2026-09-17 104930" src="https://github.com/user-attachments/assets/1c3eb5fb-c8f4-4525-8ef7-192aa7bba33e" />


