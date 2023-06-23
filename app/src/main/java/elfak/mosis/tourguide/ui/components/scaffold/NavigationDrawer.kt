package elfak.mosis.tourguide.ui.components.scaffold

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.MaterialTheme.colors
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowForward
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.List
import androidx.compose.material.icons.rounded.Notifications
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import elfak.mosis.tourguide.R
import elfak.mosis.tourguide.domain.models.menu.MenuData
import elfak.mosis.tourguide.ui.components.images.LogoImage
import elfak.mosis.tourguide.ui.navigation.Screen

val logoSize = 80.dp
val titleSize = 20.sp

@Composable
fun TourGuideNavigationDrawer(
    navController: NavController,
    menuViewModel: MenuViewModel,
    hideDrawer: () -> Unit = { }// required for closing drawer after navigating (when returnig on main drawer is open)
    // content
//    menuDefaultList: ArrayList<MenuData>
) {
    //ovo bi trebalo mozda drugacije da se radi, da se zove fun pre poziva TourGuideNavigationDrawer
    val menuDefaultList: ArrayList<MenuData> = ArrayList()
    prepareMenuList(menuDefaultList, navController, menuViewModel, hideDrawer)

    Row(modifier = Modifier
        .fillMaxWidth()
        .background(color = colors.primary)) {
        Row(modifier = Modifier
            .fillMaxWidth()
            .padding(start = 121.dp)
//            .background(color = MaterialTheme.colors.primary)
        ) {
            LogoImage(size = logoSize)
        }
    }
    Row(modifier = Modifier
        .fillMaxWidth()
        .padding(start = 105.dp)
    ) {
        Text(
            stringResource(id = R.string.welcome_screen_title).uppercase(),
            style = MaterialTheme.typography.h1,
            color = colors.primary,
            fontSize = titleSize
        )

    }
//    Spacer(modifier = Modifier.background(color = MaterialTheme.colors.background).heightIn( 1.dp ))

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 15.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ){
        items(menuDefaultList){ itemObject:MenuData ->
            MenuItemStyle(menuItem = itemObject)
        }
    }
}

@Composable
fun MenuItemStyle(menuItem: MenuData) {
    Row(
        modifier = Modifier
            .fillMaxSize()
            .clickable { menuItem.onClick() },
        horizontalArrangement = Arrangement.Start,
        verticalAlignment = Alignment.CenterVertically

    ) {
        Icon(
            imageVector = menuItem.menuIcon,
            contentDescription = menuItem.name,
            modifier = Modifier
                .size(30.dp),
            tint = colors.primary
        )
        Text(
            text = menuItem.name,
            style = MaterialTheme.typography.h6,
            color = colors.primary,
            modifier = Modifier.padding(start = 20.dp)
        )

    }
}

fun prepareMenuList(menuList: ArrayList<MenuData>, navController: NavController, viewModel: MenuViewModel, hideDrawer: () -> Unit){
//    menuList.add(
//        MenuData(
//            android.app.res.drawable.logovectorize.xml,
//            "TOUR GUIDE",
//            navigate = ( )
//
//        )
//   )


    menuList.add(
        MenuData(
            Icons.Rounded.Home,
            "Home",
            onClick = {
                navController.navigate(Screen.HomeScreen.route)
                {
                    popUpTo(Screen.HomeScreen.route) { inclusive = true }
                }
                hideDrawer()
            }
        )
    )
    menuList.add(
        MenuData(
            Icons.Rounded.Notifications,
            "Notifications",
            onClick = {
                navController.navigate(Screen.NotificationScreen.route)
                {
                    popUpTo(Screen.HomeScreen.route)
                }
                hideDrawer()
            }
        )
    )
    menuList.add(
        MenuData(
            Icons.Rounded.Person,
            "Profile",
            onClick = {
                navController.navigate(Screen.ProfileScreen.route)
                {
                    popUpTo(Screen.HomeScreen.route)
                }
                hideDrawer()
            }
        )
    )
    menuList.add(
        MenuData(
            Icons.Rounded.List,
            "Friends List",
            onClick = {
                navController.navigate(Screen.FriendsScreen.route)
                {
                    popUpTo(Screen.HomeScreen.route)
                }
                hideDrawer()
            }
        )
    )
    menuList.add(
        MenuData(
            Icons.Rounded.Settings,
            "Settings",
            onClick = {
                navController.navigate(Screen.SettingScreen.route) {
                    popUpTo(Screen.HomeScreen.route)
                }
            }
        )
    )
    menuList.add(
        MenuData(
          Icons.Rounded.ArrowForward,
          "Sign out",
          onClick = {
              viewModel.logout()
              navController.navigate(Screen.WelcomeScreen.route) {
                  popUpTo(Screen.Main.route) { inclusive = true }
              }
          }
        )
    )



}

//@Composable
//fun TourGuideNavigationDrawer(
//    // content
//) {
//    Column(
//        modifier = Modifier.fillMaxSize(),
//        verticalArrangement = Arrangement.Center,
//        horizontalAlignment = Alignment.CenterHorizontally
//    ) {
//        // text
//        Text(text = "Your UI Here")
//
//        // gap between text and button
//        Spacer(modifier = Modifier.height(height = 32.dp))
//
//        // button
//        Button(onClick = {
//            // close the drawer
//            coroutineScope.launch {
//                scaffoldState.drawerState.close()
//            }
//        }) {
//            Text(text = "Close Drawer")
//        }
//    }
//}



