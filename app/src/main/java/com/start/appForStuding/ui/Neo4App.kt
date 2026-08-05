package com.start.appForStuding.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.start.appForStuding.ui.screens.BoardListScreen
import com.start.appForStuding.ui.screens.CreateBoardScreen
import com.start.appForStuding.ui.screens.ShowBoardScreen
import com.start.appForStuding.ui.screens.UpdateBoardScreen

enum class Navigate(){
    BOARD_LIST, READ, CREATE, UPDATE
}

@Composable
fun Neo4App(innerPadding: PaddingValues) {
    val navController = rememberNavController()
    var id by remember { mutableIntStateOf(0) }
//    Scaffold()
        Box(modifier = Modifier.padding(innerPadding)) {

            NavHost(
                navController = navController, startDestination = Navigate.BOARD_LIST.name
            ) {
                composable(route = Navigate.BOARD_LIST.name) {
                    BoardListScreen(){ destination, inputId ->
                        if(inputId != null){
                            id = inputId
                        }
                        navController.navigate(destination)
                    }
                }
                composable(route = Navigate.CREATE.name) {
                    CreateBoardScreen(){
                        navController.navigate(it)
                    }
                }
                composable(route = Navigate.READ.name) {
                    ShowBoardScreen(id = id){
                        navController.navigate(it)
                    }
                }
                composable(route = Navigate.UPDATE.name) {
                    UpdateBoardScreen (id = id){
                        navController.navigate(it)
                    }
                }
            }
        }

}

//NavHost = 화면을 띄워준다.
//composable = 하나의 화면
//이 화면은, route라는 String 형태로 구현
// 내부 호스트가 home 일 땐, home을 띄우고
// home 1일 땐 home 1을 띄운다.