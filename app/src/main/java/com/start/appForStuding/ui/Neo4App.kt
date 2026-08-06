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

    /**
     * 이미 백스택에 있는 화면이면 새로 쌓지 않고 그 화면까지 되돌아간다.
     * 전부 navigate 로만 이동하면 뒤로가기를 눌러도 스택이 계속 쌓여
     * 삭제한 게시글의 상세 화면으로 되돌아가는 문제가 생긴다.
     */
    fun moveTo(route: String) {
        if (!navController.popBackStack(route, inclusive = false)) {
            navController.navigate(route)
        }
    }

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
                        moveTo(destination)
                    }
                }
                composable(route = Navigate.CREATE.name) {
                    CreateBoardScreen(){
                        moveTo(it)
                    }
                }
                composable(route = Navigate.READ.name) {
                    ShowBoardScreen(id = id){
                        moveTo(it)
                    }
                }
                composable(route = Navigate.UPDATE.name) {
                    UpdateBoardScreen (id = id){
                        moveTo(it)
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