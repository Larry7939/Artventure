package com.artventure.artventure.presentation.screen.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.artventure.artventure.R

val pretendard = FontFamily(
    Font(R.font.pretendard_b, FontWeight.Bold),
    Font(R.font.pretendard_sb, FontWeight.SemiBold),
    Font(R.font.pretendard_m, FontWeight.Medium),
)
// Set of Material typography styles to start with
val ArtventureTypography = Typography(
    headlineLarge = TextStyle(
        fontFamily = pretendard,
        fontWeight = FontWeight.Bold,
        fontSize = 28.sp
    ),
    headlineMedium = TextStyle(
        fontFamily = pretendard,
        fontWeight = FontWeight.Bold,
        fontSize = 24.sp
    ),
    headlineSmall = TextStyle(
        fontFamily = pretendard,
        fontWeight = FontWeight.SemiBold,
        fontSize = 20.sp
    ),
    bodyLarge = TextStyle(
        fontFamily = pretendard,
        fontWeight = FontWeight.SemiBold,
        fontSize = 15.sp
    ),
    displayLarge = TextStyle(
        fontFamily = pretendard,
        fontWeight = FontWeight.Medium,
        fontSize = 18.sp
    ),
    displayMedium = TextStyle(
        fontFamily = pretendard,
        fontWeight = FontWeight.Medium,
        fontSize = 15.sp
    ),
    displaySmall = TextStyle(
        fontFamily = pretendard,
        fontWeight = FontWeight.Medium,
        fontSize = 12.sp
    )
)