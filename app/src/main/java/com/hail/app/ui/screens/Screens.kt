package com.hail.app.ui.screens
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.hail.app.data.model.TestItem
import com.hail.app.data.repository.TestRepository
import com.hail.app.util.NetworkResult

@Composable fun LoginScreen(vm: LoginViewModel, onDone: () -> Unit) {
    val s by vm.state.collectAsStateWithLifecycle()
    var u by remember { mutableStateOf("") }; var p by remember { mutableStateOf("") }
    LaunchedEffect(s.done) { if (s.done) onDone() }
    Column(Modifier.fillMaxSize().padding(24.dp), Arrangement.Center) {
        Text("Hail", style = MaterialTheme.typography.displayMedium)
        Spacer(Modifier.height(24.dp))
        OutlinedTextField(u, { u = it }, label = { Text("Username / Roll Number") }, singleLine = true, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(p, { p = it }, label = { Text("Password") }, singleLine = true, visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password), modifier = Modifier.fillMaxWidth())
        s.error?.let { Text(it, color = MaterialTheme.colorScheme.error) }
        Spacer(Modifier.height(16.dp))
        Button({ vm.login(u, p) }, enabled = !s.loading, modifier = Modifier.fillMaxWidth().heightIn(min = 48.dp)) {
            Text(if (s.loading) "Logging in..." else "Login") }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable fun DashboardScreen(repo: TestRepository, onOpen: (Int) -> Unit, onLogout: () -> Unit) {
    var tests by remember { mutableStateOf<List<TestItem>?>(null) }; var err by remember { mutableStateOf<String?>(null) }
    LaunchedEffect(Unit) { when (val r = repo.assignedTests()) {
        is NetworkResult.Success -> tests = r.data
        is NetworkResult.Error -> err = r.message
        else -> {} } }
    Scaffold(topBar = { TopAppBar(title = { Text("Hail") }, actions = { TextButton(onLogout) { Text("Logout") } }) }) { pad ->
        Box(Modifier.padding(pad).fillMaxSize().padding(16.dp)) {
            when {
                err != null -> Text(err!!, color = MaterialTheme.colorScheme.error)
                tests == null -> CircularProgressIndicator(Modifier.align(Alignment.Center))
                tests!!.isEmpty() -> Text("No assigned tests")
                else -> LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) { items(tests!!) { t ->
                    ElevatedCard(Modifier.fillMaxWidth()) { Column(Modifier.padding(16.dp)) {
                        Text(t.name ?: "Untitled test", style = MaterialTheme.typography.titleMedium)
                        t.type?.let { Text(it) }
                        Button({ t.id?.let(onOpen) }, enabled = t.id != null) { Text("Open Test") } } } } }
            } } }
}

@Composable fun ExamScreen(vm: ExamViewModel, onFinished: () -> Unit) {
    val s by vm.state.collectAsStateWithLifecycle()
    var confirm by remember { mutableStateOf(false) }
    if (s.loading) { Box(Modifier.fillMaxSize(), Alignment.Center) { CircularProgressIndicator() }; return }
    if (s.submitted) { Column(Modifier.fillMaxSize().padding(24.dp), Arrangement.Center) {
        Text("Test submitted", style = MaterialTheme.typography.headlineMedium); Button(onFinished) { Text("Done") } }; return }
    val q = s.questions.getOrNull(s.index)
    Column(Modifier.fillMaxSize().padding(16.dp)) {
        Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween) {
            Text("Hail"); Text("%02d:%02d".format(s.remainingSec / 60, s.remainingSec % 60)) }
        s.error?.let { Text(it, color = MaterialTheme.colorScheme.error) }
        if (s.pending.isNotEmpty()) Text("Pending sync: ${s.pending.size}")
        if (q != null) {
            Text("Question ${s.index + 1} of ${s.questions.size}", style = MaterialTheme.typography.labelLarge)
            Text(q.text ?: "", style = MaterialTheme.typography.bodyLarge)
            q.options.forEach { o -> Row(Modifier.fillMaxWidth().heightIn(min = 48.dp)
                .selectable(s.selected[q.id] == o.id, enabled = !s.locked) { o.id?.let { vm.select(q, it) } },
                verticalAlignment = Alignment.CenterVertically) {
                RadioButton(s.selected[q.id] == o.id, null); Text(o.text ?: "", Modifier.padding(start = 8.dp)) } }
        }
        Spacer(Modifier.weight(1f))
        Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween) {
            OutlinedButton({ vm.goTo(s.index - 1) }, enabled = s.index > 0) { Text("Previous") }
            Button({ confirm = true }, enabled = !s.submitting) { Text("Submit") }
            OutlinedButton({ vm.goTo(s.index + 1) }, enabled = s.index < s.questions.lastIndex) { Text("Next") } }
    }
    if (confirm) AlertDialog({ confirm = false }, title = { Text("Submit Test?") },
        text = { Text("Answered ${s.selected.size} / ${s.questions.size}. Unanswered: ${s.questions.size - s.selected.size}") },
        confirmButton = { TextButton({ confirm = false; vm.submit() }) { Text("Submit") } },
        dismissButton = { TextButton({ confirm = false }) { Text("Cancel") } })
}
