package elfak.mosis.tourguide.ui.theme

import androidx.compose.material.MaterialTheme
import androidx.compose.material.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import elfak.mosis.tourguide.R

val Ubuntu = FontFamily(
    Font(R.font.ubuntu),
    Font(R.font.ubuntu_medium, FontWeight.Medium)
)

// Set of Material typography styles to start with
val Typography = Typography(
    subtitle1 = TextStyle(
        fontFamily = Ubuntu,
        fontWeight = FontWeight.Normal,
        fontSize = 20.sp,
    ),
    h1 = TextStyle(
        fontFamily = Ubuntu,
        fontWeight = FontWeight.Medium,
        fontSize = 30.sp,
    ),
    h2 = TextStyle(
        fontFamily = Ubuntu,
        fontWeight = FontWeight.Normal,
        fontSize = 25.sp,
    ),
    h3 = TextStyle(
        fontFamily = Ubuntu,
        fontWeight = FontWeight.Normal,
        fontSize = 22.sp,
    ),
    h4 = TextStyle(
        fontFamily = Ubuntu,
        fontWeight = FontWeight.Normal,
        fontSize = 20.sp,
    ),
    button = TextStyle(
        fontFamily = Ubuntu,
        fontWeight = FontWeight.Normal,
        fontSize = 20.sp
    ),
    body1 = TextStyle(
        fontFamily = Ubuntu,
        fontWeight = FontWeight.Thin,
        fontSize = 18.sp,
    ),
    body2 = TextStyle(
        fontFamily = Ubuntu,
        fontWeight = FontWeight.Thin,
        fontSize = 15.sp,
    ),
    h5 = TextStyle(
        fontFamily = Ubuntu,
        fontWeight = FontWeight.Thin,
        fontSize = 12.sp,
    ),
    caption = TextStyle(
        fontFamily = Ubuntu,
        fontWeight = FontWeight.Thin,
        fontSize = 18.sp,
    )
    /* Other default text styles to override
    caption = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 12.sp
    )
    */
)

