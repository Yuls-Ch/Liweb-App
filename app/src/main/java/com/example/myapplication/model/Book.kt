package com.example.myapplication.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

// Modelo de datos de libro, parcelable para pasar por Intent
@Parcelize
data class Book(
    val titleBook: String = "",
    val imageBook: String = "",
    val priceBook: Double = 0.0,
    val stock: Int = 0,
    val numberPosition: Int = 1,
    val synopsisBook: String = "",
    val nameCategory: String = "",
    var quantitySelected: Int = 1
) : Parcelable
