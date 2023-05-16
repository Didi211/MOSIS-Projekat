package elfak.mosis.tourguide.ui.components.dialogs

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import elfak.mosis.tourguide.R
import elfak.mosis.tourguide.data.models.PlaceAutocompleteResult
import elfak.mosis.tourguide.ui.components.maps.ListOfPlaces
import elfak.mosis.tourguide.ui.components.maps.SearchField

@Composable
fun SearchLocationDialog(
    onDismiss: () -> Unit,
    placesList: MutableList<PlaceAutocompleteResult>,
    onPlaceClick: (PlaceAutocompleteResult) -> Unit,
    searchForPlaces: (String) -> Unit = { }
) {
    var text by remember { mutableStateOf("")}
    Dialog(
        onDismissRequest = onDismiss
    ) {
        Column(
            modifier = Modifier
                .height(350.dp)
                .background(Color.White, RoundedCornerShape(10.dp))
                .padding(10.dp),
            verticalArrangement = Arrangement.Bottom
        ) {
            ListOfPlaces(
                placesList = placesList,
                onPlaceClick = onPlaceClick,
            )
            SearchField(
                onSearch = { },
                text = text,
                onTextChanged = {
                    text = it
                    searchForPlaces(it)
                },
                label = stringResource(id = R.string.search_here)
            )
        }
    }
}


