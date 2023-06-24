package elfak.mosis.tourguide.ui.components.dialogs

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowDropDown
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.core.text.isDigitsOnly
import com.google.android.libraries.places.api.model.PlaceTypes
import elfak.mosis.tourguide.R
import elfak.mosis.tourguide.domain.models.tour.TourConstants
import elfak.mosis.tourguide.ui.components.BasicInputComponent
import elfak.mosis.tourguide.ui.components.buttons.ButtonRowContainer
import elfak.mosis.tourguide.ui.components.buttons.CancelButton
import elfak.mosis.tourguide.ui.components.buttons.SaveButton
import es.dmoral.toasty.Toasty
import java.util.Locale


@Composable
fun CategoryFilterDialog(
    onDismiss: () -> Unit,
    onClick: (category: String, radius: Int) -> Unit,
    validate: (category: String, radius: String) -> Boolean
) {
    var selectedCategory by remember { mutableStateOf("Choose category") }
    var radius by remember { mutableStateOf("")}
    val categoryList by remember { mutableStateOf(createCategoryDropdownList()) }
    var showDropdown by remember { mutableStateOf(false) }
    val context = LocalContext.current



    Dialog(onDismissRequest = onDismiss) {

        Column(
            Modifier
                .wrapContentHeight()
                .background(Color.White, RoundedCornerShape(10.dp))
                .padding(10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Heading
            Text(
                modifier = Modifier.align(Alignment.Start),
                text = stringResource(id = R.string.filter_places) + ":",
                style = MaterialTheme.typography.h3,
                color = MaterialTheme.colors.primary
            )
            Column(
                Modifier
                    .wrapContentHeight()
                    .padding(5.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // radius
                BasicInputComponent(
                    text = radius,
                    onTextChanged = { text: String -> radius = text },
                    label = stringResource(id = R.string.search_in_radius),
                    placeholder = "Meters",
                    keyboardOptions = KeyboardOptions.Default.copy(
                        keyboardType = KeyboardType.Phone,
                    )
                )
                Spacer(Modifier.height(10.dp))

                Row(
                    Modifier
                        .fillMaxWidth()
                        .height(60.dp)
                        .padding(horizontal = 5.dp)
                        .border(
                            color = MaterialTheme.colors.primary,
                            width = 2.dp,
                            shape = RoundedCornerShape(10.dp)
                        )
                        .clickable { showDropdown = !showDropdown },
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // category dropdown
                    Text(
                        modifier = Modifier
                            .fillMaxWidth(0.9f)
                            .padding(start = 10.dp),
                        text = selectedCategory.toCategoryString(),
                    )
                    Icon(
                        imageVector = Icons.Rounded.ArrowDropDown,
                        contentDescription = stringResource(id = R.string.choose_category_dropdown_icon),
                        tint = MaterialTheme.colors.primary,
                        modifier = Modifier.fillMaxSize()
                    )
                    DropdownMenu(
                        modifier = Modifier
                            .heightIn(max = 300.dp)
                            .fillMaxWidth(0.7f),
                        expanded = showDropdown,
                        onDismissRequest = { showDropdown = false }
                    ) {
                        for (category in categoryList) {
                            DropdownMenuItem(onClick = {
                                selectedCategory = category
                                showDropdown = false
                            }) {
                                Text(text = category.toCategoryString())
                            }
                        }
                    }
                }
                Spacer(Modifier.height(20.dp))
                ButtonRowContainer {
                    SaveButton {
                        if(!validate(selectedCategory, radius)) {
                            return@SaveButton
                        }
                        onClick(selectedCategory, radius.toInt())
                    }
                    Spacer(Modifier.width(10.dp))
                    CancelButton(onDismiss)
                }

            }
        }
    }
}

private fun createCategoryDropdownList(): List<String> {
    return listOf(
        TourConstants.DefaultCategory,
        PlaceTypes.RESTAURANT,
        PlaceTypes.CAFE,
        PlaceTypes.BAKERY,
        PlaceTypes.SUPERMARKET,


        PlaceTypes.HOSPITAL,
        PlaceTypes.ATM,
        PlaceTypes.LODGING,

        PlaceTypes.ZOO,
        PlaceTypes.MUSEUM,
        PlaceTypes.AQUARIUM,
        PlaceTypes.TOURIST_ATTRACTION,
        PlaceTypes.SHOPPING_MALL,
    )
}

fun String.toCategoryString(): String {
    return this
        .replace('_', ' ')
        .replaceFirstChar {
            if (it.isLowerCase())
                it.titlecase(Locale.getDefault())
            else it.toString()
        }
}