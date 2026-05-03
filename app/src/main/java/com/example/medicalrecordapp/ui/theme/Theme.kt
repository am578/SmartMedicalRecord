package com.example.medicalrecordapp.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

//  (Dark Mode)
private val DarkColors = darkColorScheme(
    primary = BabyBlue,
    secondary = GreenAccent,
    background = DarkBackground,
    surface = DarkCard,
    onPrimary = Color.Black,
    onBackground = Color.White,
    onSurface = Color.White
)

  //(Light Mode)
  private val LightColors = lightColorScheme(
      primary = BabyBlue,
      secondary = GreenAccent,
      background = Color.White,
      surface = LightBackground,

  )

@Composable
fun MedicalRecordAppTheme(

    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColors else LightColors

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}