package elfak.mosis.tourguide.ui.components.scaffold

import android.graphics.drawable.Icon
import android.view.Menu
import android.view.MenuItem
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material.*
import androidx.compose.material.MaterialTheme.colors
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.navigation.NavController
import elfak.mosis.tourguide.ui.components.padding
import elfak.mosis.tourguide.ui.navigation.Screen
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch


@Composable
fun TourGuideNavigationDrawer(
    coroutineScope: CoroutineScope,
    scaffoldState: ScaffoldState,
    navController: NavController,
    menuViewModel: MenuViewModel
    // content
//    menuDefaultList: ArrayList<MenuData>
) {
    //ovo bi trebalo mozda drugacije da se radi, da se zove fun pre poziva TourGuideNavigationDrawer
    var menuDefaultList: ArrayList<MenuData> = ArrayList<MenuData>()
    prepareMenuList(menuDefaultList, navController, menuViewModel)

    LazyColumn( modifier = Modifier.fillMaxSize()){
        items(menuDefaultList){ itemObject:MenuData ->
            MenuItemStyle(menuItem = itemObject)
        }
    }
}
@Composable
fun MenuItemStyle(menuItem: MenuData) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(start = 30.dp, top = 40.dp),
        horizontalArrangement = Arrangement.Start,
        verticalAlignment = Alignment.CenterVertically
    ) {
        androidx.compose.material.Icon(
            imageVector = menuItem.menuIcon,
            contentDescription = menuItem.name,
            modifier = Modifier.size(30.dp).clickable { menuItem.navigate() },
            tint = colors.primary
        )
        Text(text = menuItem.name, style = MaterialTheme.typography.h6, color = colors.primary, modifier = Modifier.padding(start = 20.dp))

    }
}

fun prepareMenuList(menuList: ArrayList<MenuData>, navController: NavController, viewModel: MenuViewModel){
    menuList.add(
        MenuData(
            Icons.Rounded.Home,
            "Home",
            navigate = {
                navController.navigate(Screen.HomeScreen.route)
                {
                    popUpTo(Screen.HomeScreen.route) { inclusive = true }
                }
            }
        )
    )
    menuList.add(
        MenuData(
            Icons.Rounded.Notifications,
            "Notifications",
            navigate = {
                navController.navigate(Screen.NotificationScreen.route)
                {
                    popUpTo(Screen.HomeScreen.route)
                }
            }
        )
    )
    menuList.add(
        MenuData(
            Icons.Rounded.Person,
            "Profile",
            navigate = {
                navController.navigate(Screen.ProfileScreen.route)
                {
                    popUpTo(Screen.HomeScreen.route)
                }
            }
        )
    )
    menuList.add(
        MenuData(
            Icons.Rounded.List,
            "Friends List",
            navigate = {
                navController.navigate(Screen.FriendsScreen.route)
                {
                    popUpTo(Screen.HomeScreen.route)
                }
            }
        )
    )
    menuList.add(
        MenuData(
          Icons.Rounded.ArrowForward,
          "Sign out",
          navigate = {
              viewModel.logout()
              navController.navigate(Screen.WelcomeScreen.route) {
                  popUpTo(Screen.Main.route) { inclusive = true }
              }

          }
        )
    )

}

data class MenuData(
    val menuIcon: ImageVector,
    val name: String,
    val navigate: () -> Unit
)

