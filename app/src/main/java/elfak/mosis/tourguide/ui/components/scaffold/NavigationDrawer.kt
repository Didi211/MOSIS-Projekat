package elfak.mosis.tourguide.ui.components.scaffold

import android.graphics.drawable.Icon
import android.view.MenuItem
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.List
import androidx.compose.material.icons.rounded.Notifications
import androidx.compose.material.icons.rounded.Person
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import elfak.mosis.tourguide.ui.components.padding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch


@Composable
fun TourGuideNavigationDrawer(
    coroutineScope: CoroutineScope,
    scaffoldState: ScaffoldState,
    // content
//    menuDefaultList: ArrayList<MenuData>
) {
    //ovo bi trebalo mozda drugacije da se radi, da se zove fun pre poziva TourGuideNavigationDrawer
    var menuDefaultList: ArrayList<MenuData> = ArrayList<MenuData>()
    prepareMenuList(menuDefaultList)

    LazyColumn( modifier = Modifier.fillMaxSize()){
        items(menuDefaultList){ itemObject:MenuData ->
            MenuItemStyle(menuItem = itemObject)
        }
    }
}
@Composable
fun MenuItemStyle(menuItem: MenuData){
    Row(
        modifier = Modifier.fillMaxWidth().padding(start = 10.dp, bottom = 30.dp ),
        horizontalArrangement = Arrangement.Start,
        verticalAlignment = Alignment.CenterVertically
    ) {
        androidx.compose.material.Icon(imageVector = menuItem.menuIcon, contentDescription = menuItem.name, modifier = Modifier.size(30.dp).padding(end = 5.dp))
        Text(text = menuItem.name, style = MaterialTheme.typography.h5)

    }
}

@Composable
fun prepareMenuList(menuList: ArrayList<MenuData>){
    menuList.add(
        MenuData(
            Icons.Rounded.Home,
            "Home"
        )
    )
    menuList.add(
        MenuData(
            Icons.Rounded.Notifications,
            "Notifications"
        )
    )
    menuList.add(
        MenuData(
            Icons.Rounded.Person,
            "Profile"
        )
    )
    menuList.add(
        MenuData(
            Icons.Rounded.List,
            "Friends List"
        )
    )

}

data class MenuData(
    val menuIcon: ImageVector,
    val name: String
)

