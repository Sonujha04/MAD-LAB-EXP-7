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
