package com.example.medicalrecordapp.ui.components

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.medicalrecordapp.utils.LanguageManager

@Composable
fun LanguageSwitcherButton(
    currentLang: String,
    onLanguageChange: (String) -> Unit
) {
    val nextLang = if (currentLang == LanguageManager.LANG_AR) LanguageManager.LANG_EN else LanguageManager.LANG_AR
    val label = if (currentLang == LanguageManager.LANG_AR) "EN" else "ع"

    Surface(
        onClick = { onLanguageChange(nextLang) },
        shape = RoundedCornerShape(8.dp),
        color = Color.White.copy(alpha = 0.2f)
    ) {
        Text(
            text = label,
            fontWeight = FontWeight.Bold,
            color = Color.White,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
        )
    }
}