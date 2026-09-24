package com.hail.app
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.*
import androidx.navigation.navArgument
import com.hail.app.ui.screens.*
import com.hail.app.ui.theme.HailTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val app = application as HailApplication
        setContent { HailTheme {
            val nav = rememberNavController()
            NavHost(nav, startDestination = if (app.session.isLoggedIn()) "dashboard" else "login") {
                composable("login") {
                    val vm: LoginViewModel = viewModel(factory = f { LoginViewModel(app.auth) })
                    LoginScreen(vm) { nav.navigate("dashboard") { popUpTo("login") { inclusive = true } } } }
                composable("dashboard") {
                    DashboardScreen(app.tests, { nav.navigate("exam/$it") }) {
                        app.auth.logout(); nav.navigate("login") { popUpTo(0) } } }
                composable("exam/{testId}", listOf(navArgument("testId") { type = NavType.IntType })) { e ->
                    val id = e.arguments!!.getInt("testId")
                    var leave by remember { mutableStateOf(false) }
                    BackHandler { leave = true }
                    if (leave) AlertDialog({ leave = false }, title = { Text("Leave test?") },
                        text = { Text("Your current answers are being saved.") },
                        confirmButton = { TextButton({ nav.popBackStack() }) { Text("Leave") } },
                        dismissButton = { TextButton({ leave = false }) { Text("Stay") } })
                    // TODO: studentId comes from the login/profile response; duration from test details.
                    val vm: ExamViewModel = viewModel(factory = f { ExamViewModel(app.tests, id, 0, 3600) })
                    ExamScreen(vm) { nav.popBackStack("dashboard", false) } }
            } } }
    }
    private fun <T : ViewModel> f(make: () -> T) = object : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST") override fun <V : ViewModel> create(modelClass: Class<V>) = make() as V }
}
