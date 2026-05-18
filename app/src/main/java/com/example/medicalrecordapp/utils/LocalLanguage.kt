package com.example.medicalrecordapp.utils

import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.mutableStateOf

val LocalLanguage = compositionLocalOf { mutableStateOf("ar") }